package com.github.prontera.service;

import com.github.prontera.EventDrivenPublisher;
import com.github.prontera.Shift;
import com.github.prontera.config.EventBusinessType;
import com.github.prontera.controller.StatusCode;
import com.github.prontera.controller.client.AccountClient;
import com.github.prontera.controller.client.ProductClient;
import com.github.prontera.controller.client.TccClient;
import com.github.prontera.domain.Order;
import com.github.prontera.domain.OrderConflict;
import com.github.prontera.domain.OrderParticipant;
import com.github.prontera.exception.PartialConfirmException;
import com.github.prontera.exception.ReservationExpireException;
import com.github.prontera.model.Participant;
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
import com.github.prontera.persistence.OrderParticipantMapper;
import com.github.prontera.util.Jacksons;
import com.github.prontera.util.OrikaMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private OrderParticipantMapper participantMapper;
    @Autowired
    private EventDrivenPublisher publisher;

    @Autowired
    public OrderService(CrudMapper<Order> mapper) {
        super(mapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public ObjectDataResponse<Order> placeOrder(PlaceOrderRequest request) {
        Preconditions.checkNotNull(request);
        final Long userId = Preconditions.checkNotNull(request.getUserId());
        final Long productId = Preconditions.checkNotNull(request.getProductId());
        // 获取产品
        final Product product = findRemoteProduct(productId);
        // 查询用户
        final User user = findRemoteUser(userId);
        // 检查余额
        if (user.getBalance() - product.getPrice() < 0) {
            Shift.fatal(StatusCode.INSUFFICIENT_BALANCE);
        }
        // 构建订单
        final Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        order.setPrice(product.getPrice());
        order.setStatus(OrderStatus.PROCESSING);
        super.persistNonNullProperties(order);
        // 预留库存
        reserveProductAndPersistParticipant(order);
        // 预留余额
        reserveBalanceAndPersistParticipant(order);
        return new ObjectDataResponse<>(order);
    }

    private void reserveBalanceAndPersistParticipant(Order order) {
        Preconditions.checkNotNull(order);
        final ReservationResponse balanceResponse = reserveBalance(order);
        // 判断是否try失败
        final Participant participant = balanceResponse.getParticipantLink();
        if (participant == null) {
            Shift.fatal(StatusCode.INSUFFICIENT_BALANCE);
        }
        persistParticipant(participant, order.getId());
    }

    private void reserveProductAndPersistParticipant(Order order) {
        Preconditions.checkNotNull(order);
        final ReservationResponse stockResponse = reserveProduct(order);
        // 判断是否try失败
        final Participant participant = stockResponse.getParticipantLink();
        if (participant == null) {
            Shift.fatal(StatusCode.INSUFFICIENT_PRODUCT);
        }
        persistParticipant(participant, order.getId());
    }

    private void persistParticipant(Participant participant, Long orderId) {
        Preconditions.checkNotNull(participant);
        Preconditions.checkNotNull(orderId);
        final OrderParticipant orderParticipant = OrikaMapper.map(participant, OrderParticipant.class);
        orderParticipant.setOrderId(orderId);
        participantMapper.insertSelective(orderParticipant);
    }

    private User findRemoteUser(Long userId) {
        Preconditions.checkNotNull(userId);
        final User user = accountClient.findUser(userId).getData();
        if (user == null) {
            Shift.fatal(StatusCode.USER_NOT_EXISTS);
        }
        return user;
    }

    private Product findRemoteProduct(Long productId) {
        Preconditions.checkNotNull(productId);
        final Product product = productClient.findProduct(productId).getData();
        if (product == null) {
            Shift.fatal(StatusCode.PRODUCT_NOT_EXISTS);
        }
        // 检查库存
        if (product.getStock() <= 0) {
            Shift.fatal(StatusCode.INSUFFICIENT_PRODUCT);
        }
        return product;
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
        final List<OrderParticipant> participants = participantMapper.selectByOrderId(orderId);
        if (participants.isEmpty()) {
            LOGGER.error("order id '{}' does not reserve any resource", orderId);
            Shift.fatal(StatusCode.SERVER_UNKNOWN_ERROR);
        }
        if (order.getStatus() == OrderStatus.PROCESSING) {
            confirmPhase(order, participants);
        }
        return new ObjectDataResponse<>(order);
    }

    private void confirmPhase(Order order, List<OrderParticipant> participants) {
        Preconditions.checkNotNull(order);
        Preconditions.checkNotNull(participants);
        Preconditions.checkArgument(!participants.isEmpty());
        // 表示全部try成功, 现在进行确认操作
        final ImmutableList<OrderParticipant> links = ImmutableList.copyOf(participants);
        final TccRequest tccRequest = new TccRequest(links);
        try {
            tccClient.confirm(tccRequest);
            order.setStatus(OrderStatus.DONE);
            if (super.updateNonNullProperties(order) > 0) {
                final ImmutableMap.Builder<String, Object> payloadBuilder = ImmutableMap.builder();
                payloadBuilder.put("point", order.getPrice());
                payloadBuilder.put("order_id", order.getId());
                payloadBuilder.put("user_id", order.getUserId());
                payloadBuilder.put("product_id", order.getProductId());
                // 发送积分添加事件
                publisher.persistPublishMessage(Jacksons.parse(payloadBuilder.build()), EventBusinessType.ADD_PTS.name());
            }
        } catch (HystrixRuntimeException e) {
            final Class<? extends Throwable> exceptionCause = e.getCause().getClass();
            if (ReservationExpireException.class.isAssignableFrom(exceptionCause)) {
                // 全部确认预留超时
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
