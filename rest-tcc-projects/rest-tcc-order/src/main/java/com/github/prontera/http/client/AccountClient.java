package com.github.prontera.http.client;

import com.github.prontera.account.enums.StatusCode;
import com.github.prontera.account.model.request.BalanceReservingRequest;
import com.github.prontera.account.model.request.ConfirmAccountTxnRequest;
import com.github.prontera.account.model.request.QueryAccountRequest;
import com.github.prontera.account.model.request.QueryAccountTxnRequest;
import com.github.prontera.account.model.response.BalanceReservingResponse;
import com.github.prontera.account.model.response.ConfirmAccountTxnResponse;
import com.github.prontera.account.model.response.QueryAccountResponse;
import com.github.prontera.account.model.response.QueryAccountTxnResponse;
import com.github.prontera.enums.NumericStatusCode;
import com.github.prontera.exception.ResolvableStatusException;
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
public class AccountClient {

    private static final Logger LOGGER = LogManager.getLogger(AccountClient.class);

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);

    private final WebClient client;

    @Lazy
    @Autowired
    public AccountClient(@Nonnull WebClient.Builder webClientBuilder) {
        this.client = webClientBuilder.baseUrl("http://account").build();
    }

    public CompletableFuture<QueryAccountResponse> queryAccountByName(@Nonnull String username) {
        Objects.requireNonNull(username);
        final QueryAccountRequest request = new QueryAccountRequest();
        request.setName(username);
        return client.post()
            .uri("/query-by-username")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(QueryAccountResponse.class)
            .timeout(DEFAULT_TIMEOUT)
            .toFuture()
            .thenApply(response -> {
                LOGGER.debug("method queryAccountByName. request '{}', response '{}'", request, response);
                if (Objects.equals(StatusCode.USER_NOT_EXISTS.code(), response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.USER_NOT_EXISTS);
                }
                if (!NumericStatusCode.isSuccessful(response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.UNKNOWN_RPC_RESPONSE);
                }
                return response;
            });
    }

    public CompletableFuture<BalanceReservingResponse> reservingBalance(@Nonnull String username,
                                                                        long orderId,
                                                                        int amount,
                                                                        int reservingSeconds) {
        Objects.requireNonNull(username);
        Preconditions.checkArgument(orderId > 0);
        Preconditions.checkArgument(amount > 0);
        Preconditions.checkArgument(reservingSeconds > 0);
        final BalanceReservingRequest request = new BalanceReservingRequest();
        request.setUsername(username);
        request.setOrderId(orderId);
        request.setAmount(amount);
        request.setExpectedReservingSeconds(reservingSeconds);
        return client.post()
            .uri("/reserve-balance")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(BalanceReservingResponse.class)
            .timeout(DEFAULT_TIMEOUT)
            .toFuture()
            .thenApply(response -> {
                LOGGER.debug("method reservingBalance. request '{}', response '{}'", request, response);
                if (Objects.equals(StatusCode.USER_NOT_EXISTS.code(), response.getCode())) {
                    throw new ResolvableStatusException(com.github.prontera.enums.StatusCode.USER_NOT_EXISTS);
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

    public CompletableFuture<ConfirmAccountTxnResponse> confirm(long orderId) {
        Preconditions.checkArgument(orderId > 0);
        final ConfirmAccountTxnRequest request = new ConfirmAccountTxnRequest();
        request.setOrderId(orderId);
        return client.post()
            .uri("/confirm-transaction")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ConfirmAccountTxnResponse.class)
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

    public CompletableFuture<QueryAccountTxnResponse> queryTransaction(long orderId) {
        Preconditions.checkArgument(orderId > 0);
        final QueryAccountTxnRequest request = new QueryAccountTxnRequest();
        request.setOrderId(orderId);
        return client.post()
            .uri("/query-transaction")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(QueryAccountTxnResponse.class)
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
