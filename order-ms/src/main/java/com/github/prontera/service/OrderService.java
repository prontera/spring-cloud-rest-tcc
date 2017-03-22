package com.github.prontera.service;

import com.github.prontera.Shift;
import com.github.prontera.controller.StatusCode;
import com.github.prontera.controller.client.AccountClient;
import com.github.prontera.controller.client.ProductClient;
import com.github.prontera.controller.client.TccClient;
import com.github.prontera.domain.Order;
import com.github.prontera.domain.OrderConflict;
import com.github.prontera.exception.PartialConfirmException;
import com.github.prontera.exception.ReservationExpireException;
import com.github.prontera.domain.Participant;
import com.github.prontera.model.Product;
import com.github.prontera.model.User;
import com.github.prontera.model.request.BalanceReservationRequest;
import com.github.prontera.model.request.PaymentRequest;
import com.github.prontera.model.request.PlaceOrderRequest;
import com.github.prontera.model.request.StockReservationRequest;
import com.github.prontera.model.request.TccRequest;
import com.github.prontera.model.response.ObjectDataResponse;
import com.github.prontera.model.response.ReservationResponse;
import com.github.prontera.model.type.OrderStatus;
import com.github.prontera.persistence.CrudMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhao Junjian
 */
@Service
public class OrderService extends CrudServiceImpl<Order> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private TccClient tccClient;
    @Autowired
    private AccountClient accountClient;
    @Autowired
    private ProductClient productClient;
    @Autowired
    private OrderConflictService conflictService;

    @Autowired
    public OrderService(CrudMapper<Order> mapper) {
        super(mapper);
    }

    /**
     * 本处故意不检查库存与余额, 以便测试tcc
     */
    @Transactional(rollbackFor = Exception.class)
    public ObjectDataResponse<Order> placeOrder(PlaceOrderRequest request) {
        Preconditions.checkNotNull(request);
        final Long userId = Preconditions.checkNotNull(request.getUserId());
        final Long productId = Preconditions.checkNotNull(request.getProductId());
        // 查询产品价格
        final Product product = productClient.findProduct(productId).getData();
        if (product == null) {
            Shift.fatal(StatusCode.PRODUCT_NOT_EXISTS);
        }
        // 查询用户
        final User user = accountClient.findUser(userId).getData();
        if (user == null) {
            Shift.fatal(StatusCode.USER_NOT_EXISTS);
        }
        // 构建订单
        final Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setPrice(product.getPrice());
        order.setStatus(OrderStatus.PROCESSING);
        super.persistNonNullProperties(order);
        return new ObjectDataResponse<>(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public ObjectDataResponse<Order> confirm(PaymentRequest request) {
        Preconditions.checkNotNull(request);
        final Long orderId = request.getOrderId();
        // 检查订单是否存在
        final Order order = super.find(orderId);
        if (order == null) {
            Shift.fatal(StatusCode.ORDER_NOT_EXISTS);
        }
        if (order.getStatus() == OrderStatus.PROCESSING) {
            // 预留库存
            final ReservationResponse stockResponse = reserveProduct(order);
            // 判断是否try失败
            if (stockResponse.getParticipantLink() == null) {
                order.setStatus(OrderStatus.INSUFFICIENT_STOCK);
                super.updateNonNullProperties(order);
            } else {
                // 预留余额
                final ReservationResponse balanceResponse = reserveBalance(order);
                // 判断是否try失败
                if (balanceResponse.getParticipantLink() == null) {
                    order.setStatus(OrderStatus.INSUFFICIENT_BALANCE);
                    super.updateNonNullProperties(order);
                } else {
                    // 成功后进行确认操作
                    confirmPhase(order, stockResponse, balanceResponse);
                }
            }
        }
        return new ObjectDataResponse<>(order);
    }

    private void confirmPhase(Order order, ReservationResponse stockResponse, ReservationResponse balanceResponse) {
        Preconditions.checkNotNull(order);
        Preconditions.checkNotNull(stockResponse);
        Preconditions.checkNotNull(balanceResponse);
        // 表示全部try成功, 现在进行确认操作
        final ImmutableList<Participant> links = ImmutableList.of(stockResponse.getParticipantLink(), balanceResponse.getParticipantLink());
        final TccRequest tccRequest = new TccRequest(links);
        try {
            tccClient.confirm(tccRequest);
            order.setStatus(OrderStatus.DONE);
            super.updateNonNullProperties(order);
        } catch (HystrixRuntimeException e) {
            final Class<? extends Throwable> exceptionCause = e.getCause().getClass();
            if (ReservationExpireException.class.isAssignableFrom(exceptionCause)) {
                order.setStatus(OrderStatus.TIMEOUT);
                super.updateNonNullProperties(order);
            } else if (PartialConfirmException.class.isAssignableFrom(exceptionCause)) {
                order.setStatus(OrderStatus.CONFLICT);
                super.updateNonNullProperties(order);
                markdownConfliction(order, e);
            } else {
                throw e;
            }
        }
    }

    private void markdownConfliction(Order order, HystrixRuntimeException e) {
        Preconditions.checkNotNull(order);
        Preconditions.checkNotNull(e);
        final String message = e.getCause().getMessage();
        LOGGER.error("order id '{}' has come across an confliction. {}", order.getId(), message);
        final OrderConflict conflict = new OrderConflict();
        conflict.setOrderId(order.getId());
        conflict.setErrorDetail(message);
        conflictService.persistNonNullProperties(conflict);
    }

    private ReservationResponse reserveBalance(Order order) {
        Preconditions.checkNotNull(order);
        final BalanceReservationRequest balanceReservation = new BalanceReservationRequest();
        balanceReservation.setUserId(order.getUserId());
        balanceReservation.setAmount(Long.valueOf(order.getPrice()));
        return accountClient.reserve(balanceReservation);
    }

    private ReservationResponse reserveProduct(Order order) {
        Preconditions.checkNotNull(order);
        final StockReservationRequest reservation = new StockReservationRequest();
        reservation.setProductId(order.getProductId());
        return productClient.reserve(reservation);
    }

}
