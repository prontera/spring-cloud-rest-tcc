package com.github.prontera.service;

import com.github.prontera.account.enums.ReservingState;
import com.github.prontera.account.model.response.BalanceReservingResponse;
import com.github.prontera.account.model.response.ConfirmAccountTxnResponse;
import com.github.prontera.account.model.response.QueryAccountResponse;
import com.github.prontera.concurrent.Pools;
import com.github.prontera.domain.Order;
import com.github.prontera.enums.NumericStatusCode;
import com.github.prontera.enums.OrderState;
import com.github.prontera.enums.StatusCode;
import com.github.prontera.exception.ApplicationException;
import com.github.prontera.exception.ResolvableStatusException;
import com.github.prontera.http.client.AccountClient;
import com.github.prontera.http.client.ProductClient;
import com.github.prontera.model.request.CheckoutRequest;
import com.github.prontera.model.request.DiagnoseRequest;
import com.github.prontera.model.response.CheckoutResponse;
import com.github.prontera.model.response.DiagnoseResponse;
import com.github.prontera.model.response.ResolvableResponse;
import com.github.prontera.persistence.OrderMapper;
import com.github.prontera.product.model.response.ConfirmProductTxnResponse;
import com.github.prontera.product.model.response.InventoryReservingResponse;
import com.github.prontera.product.model.response.QueryProductResponse;
import com.github.prontera.util.Responses;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author Zhao Junjian
 * @date 2020/01/21
 */
@Service
public class OrderService {

    private static final Logger LOGGER = LogManager.getLogger(OrderService.class);

    private static final int RESERVING_IN_SECS = 5;

    private static final int COMPENSATION_IN_SECS = 3;

    private final OrderMapper orderMapper;

    private final AccountClient accountClient;

    private final ProductClient productClient;

    @Lazy
    @Autowired
    public OrderService(@Nonnull OrderMapper orderMapper,
                        @Nonnull AccountClient accountClient,
                        @Nonnull ProductClient productClient) {
        this.orderMapper = Objects.requireNonNull(orderMapper);
        this.accountClient = accountClient;
        this.productClient = productClient;
    }

    private static <T extends ResolvableResponse> T reassembleIfResolvable(@Nonnull Throwable e, @Nonnull Class<T> returnType) {
        Objects.requireNonNull(e);
        Objects.requireNonNull(returnType);
        ResolvableStatusException rse = null;
        if (e instanceof ResolvableStatusException) {
            rse = (ResolvableStatusException) e;
        } else if (e.getCause() instanceof ResolvableStatusException) {
            rse = (ResolvableStatusException) e.getCause();
        }
        if (rse != null) {
            return Responses.generate(returnType, rse.getStatusCode());
        }
        throw new ApplicationException(e);
    }

    public CompletableFuture<DiagnoseResponse> diagnose(@Nonnull DiagnoseRequest request) {
        Objects.requireNonNull(request);
        final Order order = orderMapper.selectByGuid(request.getGuid());
        if (order == null) {
            throw new ResolvableStatusException(StatusCode.ORDER_NOT_EXISTS);
        }
        final Long orderId = order.getId();
        return accountClient.queryTransaction(orderId)
            .thenCombine(productClient.queryTransaction(orderId), (r1, r2) -> {
                final Map<String, String> map = Maps.newHashMapWithExpectedSize(2);
                map.put("account", ReservingState.parse(r1.getState()).name());
                map.put("product", com.github.prontera.product.enums.ReservingState.parse(r2.getState()).name());
                final DiagnoseResponse response = Responses.generate(DiagnoseResponse.class, StatusCode.OK);
                response.setStateMap(map);
                return response;
            }).exceptionally(e -> reassembleIfResolvable(e, DiagnoseResponse.class));
    }

    public CompletableFuture<CheckoutResponse> checkout(@Nonnull CheckoutRequest request) {
        Objects.requireNonNull(request);
        return doCheckout(request).thenApply(state -> {
            Preconditions.checkArgument(state.isFinalState());
            final CheckoutResponse response;
            if (state == OrderState.CONFIRMED) {
                response = Responses.generate(CheckoutResponse.class, StatusCode.OK);
            } else if (state == OrderState.CANCELLED) {
                response = Responses.generate(CheckoutResponse.class, StatusCode.CANCEL);
            } else if (state == OrderState.CONFLICT) {
                response = Responses.generate(CheckoutResponse.class, StatusCode.CONFLICT);
            } else {
                response = Responses.generate(CheckoutResponse.class, StatusCode.UNKNOWN_RESERVING_STATE);
            }
            return response;
        }).exceptionally(e -> reassembleIfResolvable(e, CheckoutResponse.class));
    }

    public CompletableFuture<OrderState> doCheckout(@Nonnull CheckoutRequest request) {
        Objects.requireNonNull(request);
        // check if exists the corresponding GUID for idempotency
        final Long guid = request.getGuid();
        final Order order = orderMapper.selectByGuid(guid);
        final CompletableFuture<OrderState> response;
        if (order != null) {
            LOGGER.debug("method doCheckout. recovering order that guid is '{}' and order is '{}'", guid, order);
            final OrderState persistedState = order.getState();
            Preconditions.checkArgument(persistedState != OrderState.INVALID);
            if (persistedState.isFinalState()) {
                LOGGER.debug("method doCheckout. guid '{}' order has already been a final state...", guid);
                return CompletableFuture.completedFuture(persistedState);
            }
            Preconditions.checkArgument(persistedState == OrderState.PENDING);
            // check expired time first
            if (order.getExpireAt().isBefore(LocalDateTime.now())) {
                // try reserving
                LOGGER.debug("method doCheckout. recovering and reserving order, guid '{}'", guid);
                response = CompletableFuture.completedFuture(order)
                    .thenCompose(x -> beginTccTransaction(x.getId(), request));
            } else {
                final Long orderId = order.getId();
                // just make it TIMEOUT final state and let participants cancel-automatically
                OrderState state = OrderState.CANCELLED;
                if (orderMapper.compareAndSetState(orderId, OrderState.PENDING, OrderState.CANCELLED) <= 0) {
                    // ATTENTION: u should force to retrieve from master node in production environment.
                    state = orderMapper.selectByPrimaryKey(orderId).getState();
                } else {
                    LOGGER.debug("method doCheckout. the order of guid '{}' was cancelled", guid);
                }
                response = CompletableFuture.completedFuture(state);
            }
        } else {
            // new beginning for a new order
            LOGGER.debug("method doCheckout. new tcc transaction for guid '{}'", guid);
            response = generatePendingOrder(request)
                .thenCompose(x -> beginTccTransaction(x.getId(), request));
        }
        return response;
    }

    CompletableFuture<Order> generatePendingOrder(@Nonnull CheckoutRequest request) {
        final String username = request.getUsername();
        final String productName = request.getProductName();
        final CompletableFuture<QueryAccountResponse> usernameResponse = accountClient.queryAccountByName(username);
        final CompletableFuture<QueryProductResponse> productNameResponse = productClient.queryProductByName(productName);
        return usernameResponse.thenCombineAsync(productNameResponse, (r1, r2) -> {
            final Long userId = r1.getId();
            final Long productId = r2.getId();
            // persist new order
            final Order order = new Order();
            order.setProductId(productId);
            order.setUserId(userId);
            order.setQuantity(request.getQuantity());
            order.setPrice(request.getPrice());
            order.setGuid(request.getGuid());
            final LocalDateTime now = LocalDateTime.now();
            order.setCreateAt(now);
            order.setUpdateAt(now);
            order.setExpireAt(now.plusSeconds(RESERVING_IN_SECS));
            order.setState(OrderState.PENDING);
            orderMapper.insertSelective(order);
            LOGGER.debug("method generatePendingOrder. persist a new order '{}'", order);
            return order;
        }, Pools.IO);
    }

    CompletableFuture<OrderState> beginTccTransaction(long orderId, CheckoutRequest request) {
        // try reserving
        final String username = request.getUsername();
        final String productName = request.getProductName();
        final Integer price = request.getPrice();
        final Integer quantity = request.getQuantity();
        final int reservingSeconds = RESERVING_IN_SECS + COMPENSATION_IN_SECS;
        final CompletableFuture<BalanceReservingResponse> accountReservingResponse =
            accountClient.reservingBalance(username, orderId, price, reservingSeconds);
        final CompletableFuture<InventoryReservingResponse> productReservingResponse =
            productClient.reservingInventory(productName, orderId, quantity, reservingSeconds);
        return accountReservingResponse.thenCombine(productReservingResponse, (r1, r2) -> null)
            .thenCompose(x -> confirmTransaction(orderId, username, productName, reservingSeconds));
    }

    private CompletableFuture<OrderState> confirmTransaction(long orderId, String username, String productName, int reservingSeconds) {
        return confirmAccountTransaction(orderId, username, reservingSeconds)
            .thenCombine(confirmProductTransaction(orderId, productName, reservingSeconds), (r1, r2) -> {
                OrderState state;
                if (NumericStatusCode.isSuccessful(r1.getCode()) && NumericStatusCode.isSuccessful(r2.getCode())) {
                    state = OrderState.CONFIRMED;
                } else if (Objects.equals(com.github.prontera.account.enums.StatusCode.TIMEOUT_AND_CANCELLED.code(), r1.getCode()) &&
                    Objects.equals(com.github.prontera.product.enums.StatusCode.TIMEOUT_AND_CANCELLED.code(), r2.getCode())) {
                    state = OrderState.CANCELLED;
                } else {
                    state = OrderState.CONFLICT;
                }
                if (orderMapper.compareAndSetState(orderId, OrderState.PENDING, state) <= 0) {
                    // ATTENTION: u should force to retrieve from master node in production environment.
                    state = orderMapper.selectByPrimaryKey(orderId).getState();
                } else {
                    LOGGER.debug("method confirmTransaction. order id '{}' has {}", orderId, state);
                }
                return state;
                // we should define a more specific definition for partial confirm in product environment.
            });
    }

    private CompletableFuture<ConfirmProductTxnResponse> confirmProductTransaction(long orderId, String productName, int reservingSeconds) {
        return CompletableFuture.runAsync(() -> {
            if (Objects.equals("gba", productName)) {
                LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(reservingSeconds + 1));
            }
        }, Pools.IO).thenCompose(v -> productClient.confirm(orderId));
    }

    private CompletableFuture<ConfirmAccountTxnResponse> confirmAccountTransaction(long orderId, String username, int reservingSeconds) {
        return CompletableFuture.runAsync(() -> {
            if (Objects.equals("scott", username)) {
                LockSupport.parkNanos(this, TimeUnit.SECONDS.toNanos(reservingSeconds + 1));
            }
        }, Pools.IO).thenCompose(v -> accountClient.confirm(orderId));
    }

}
