package com.github.prontera.http.client;

import com.github.prontera.enums.NumericStatusCode;
import com.github.prontera.exception.ResolvableStatusException;
import com.github.prontera.product.enums.StatusCode;
import com.github.prontera.product.model.request.ConfirmProductTxnRequest;
import com.github.prontera.product.model.request.InventoryReservingRequest;
import com.github.prontera.product.model.request.QueryProductRequest;
import com.github.prontera.product.model.request.QueryProductTxnRequest;
import com.github.prontera.product.model.response.ConfirmProductTxnResponse;
import com.github.prontera.product.model.response.InventoryReservingResponse;
import com.github.prontera.product.model.response.QueryProductResponse;
import com.github.prontera.product.model.response.QueryProductTxnResponse;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author Zhao Junjian
 * @date 2020/01/28
 */
@Component
public class ProductClient {

    private static final Logger LOGGER = LogManager.getLogger(ProductClient.class);

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);

    private final WebClient client;

    @Lazy
    @Autowired
    public ProductClient(@Nonnull WebClient.Builder webClientBuilder) {
        this.client = webClientBuilder.baseUrl("http://product").build();
    }

    public CompletableFuture<QueryProductResponse> queryProductByName(@Nonnull String name) {
        Objects.requireNonNull(name);
        final QueryProductRequest request = new QueryProductRequest();
        request.setProductName(name);
        return client.post()
            .uri("/query-by-product-name")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(QueryProductResponse.class)
            .timeout(DEFAULT_TIMEOUT)
            .toFuture()
            .thenApply(response -> {
                LOGGER.debug("method queryProductByName. request '{}', response '{}'", request, response);
                if (Objects.equals(StatusCode.PRODUCT_NOT_EXISTS.code(), response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.PRODUCT_NOT_EXISTS);
                }
                if (!NumericStatusCode.isSuccessful(response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.UNKNOWN_RPC_RESPONSE);
                }
                return response;
            });
    }

    public CompletableFuture<InventoryReservingResponse> reservingInventory(@Nonnull String name,
                                                                            long orderId,
                                                                            int amount,
                                                                            int reservingSeconds) {
        Objects.requireNonNull(name);
        Preconditions.checkArgument(orderId > 0);
        Preconditions.checkArgument(amount > 0);
        Preconditions.checkArgument(reservingSeconds > 0);
        final InventoryReservingRequest request = new InventoryReservingRequest();
        request.setProductName(name);
        request.setOrderId(orderId);
        request.setAmount(amount);
        request.setExpectedReservingSeconds(reservingSeconds);
        return client.post()
            .uri("/reserve-inventory")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(InventoryReservingResponse.class)
            .timeout(DEFAULT_TIMEOUT)
            .toFuture()
            .thenApply(response -> {
                LOGGER.debug("method reservingInventory. request '{}', response '{}'", request, response);
                if (Objects.equals(StatusCode.PRODUCT_NOT_EXISTS.code(), response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.PRODUCT_NOT_EXISTS);
                }
                if (Objects.equals(StatusCode.TIMEOUT_AND_CANCELLED.code(), response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.RESOURCE_CANCELLED);
                }
                if (Objects.equals(StatusCode.RESERVING_FINAL_STATE.code(), response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.RESOURCE_CAN_NOT_BE_RESERVED);
                }
                if (!NumericStatusCode.isSuccessful(response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.UNKNOWN_RPC_RESPONSE);
                }
                return response;
            });
    }

    public CompletableFuture<ConfirmProductTxnResponse> confirm(long orderId) {
        Preconditions.checkArgument(orderId > 0);
        final ConfirmProductTxnRequest request = new ConfirmProductTxnRequest();
        request.setOrderId(orderId);
        return client.post()
            .uri("/confirm-transaction")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ConfirmProductTxnResponse.class)
            .timeout(DEFAULT_TIMEOUT)
            .toFuture()
            .thenApply(response -> {
                LOGGER.debug("method confirm. request '{}', response '{}'", request, response);
                if (Objects.equals(StatusCode.ORDER_NOT_EXISTS.code(), response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.ORDER_NOT_EXISTS);
                }
                if (Objects.equals(StatusCode.TIMEOUT_AND_CANCELLED.code(), response.getCode())) {
                    return response;
                }
                if (!NumericStatusCode.isSuccessful(response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.UNKNOWN_RPC_RESPONSE);
                }
                return response;
            });
    }

    public CompletableFuture<QueryProductTxnResponse> queryTransaction(long orderId) {
        Preconditions.checkArgument(orderId > 0);
        final QueryProductTxnRequest request = new QueryProductTxnRequest();
        request.setOrderId(orderId);
        return client.post()
            .uri("/query-transaction")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(QueryProductTxnResponse.class)
            .timeout(DEFAULT_TIMEOUT)
            .toFuture()
            .thenApply(response -> {
                LOGGER.debug("method queryTransaction. request '{}', response '{}'", request, response);
                if (Objects.equals(StatusCode.ORDER_NOT_EXISTS.code(), response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.ORDER_NOT_EXISTS);
                }
                if (Objects.equals(StatusCode.TIMEOUT_AND_CANCELLED.code(), response.getCode())) {
                    return response;
                }
                if (!NumericStatusCode.isSuccessful(response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.UNKNOWN_RPC_RESPONSE);
                }
                return response;
            });
    }

}
