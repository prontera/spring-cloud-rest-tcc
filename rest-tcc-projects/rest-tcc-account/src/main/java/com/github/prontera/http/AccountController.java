package com.github.prontera.http;

import com.github.prontera.Shifts;
import com.github.prontera.account.model.request.BalanceReservingRequest;
import com.github.prontera.account.model.request.ConfirmAccountTxnRequest;
import com.github.prontera.account.model.request.QueryAccountTxnRequest;
import com.github.prontera.account.model.request.SignUpRequest;
import com.github.prontera.account.model.response.BalanceReservingResponse;
import com.github.prontera.account.model.response.ConfirmAccountTxnResponse;
import com.github.prontera.account.model.response.QueryAccountTxnResponse;
import com.github.prontera.account.model.response.SignUpResponse;
import com.github.prontera.annotation.FaultBarrier;
import com.github.prontera.domain.AccountTransaction;
import com.github.prontera.enums.StatusCode;
import com.github.prontera.mapper.AccountTransactionCopier;
import com.github.prontera.service.AccountService;
import com.github.prontera.util.HibernateValidators;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

/**
 * 下游不负责状态的自动轮转, 为简化Participant的职责, 只需要提高被动轮转的接口即可,
 * 例如本类中的{@link #queryTransaction(QueryAccountTxnRequest)}与{@link #confirmTransaction(ConfirmAccountTxnRequest)}
 * 都具备被动轮转的功能, 等待上游的tcc coordinator进行驱动即可
 *
 * @author Zhao Junjian
 * @date 2020/01/22
 */
@Api(tags = "Account-Debugger")
@RestController
@RequestMapping(value = "/accounts", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.ALL_VALUE})
public class AccountController {

    private final AccountService service;

    @Lazy
    @Autowired
    public AccountController(@Nonnull AccountService service) {
        this.service = Objects.requireNonNull(service);
    }

    @FaultBarrier
    @ApiOperation(value = "注册", notes = "新账号默认资金为100,000,000")
    @PostMapping(value = "/sign-up")
    public SignUpResponse signUp(@Nonnull SignUpRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        return service.signUp(request);
    }

    @FaultBarrier
    @ApiOperation(value = "预留资源, 锁定资金", notes = "_")
    @PostMapping(value = "/reserve-balance")
    public BalanceReservingResponse reserveBalance(@Nonnull BalanceReservingRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        return service.reserving(request);
    }

    @FaultBarrier
    @ApiOperation(value = "根据订单ID查询预留资源", notes = "如果发现预留资源过了保护期, 将自动归还资金, 具备fsm被动轮状的能力")
    @GetMapping(value = "/query-transaction")
    public QueryAccountTxnResponse queryTransaction(@Nonnull QueryAccountTxnRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        final Optional<AccountTransaction> nullableTxn = service.cancellableFindTransaction(request.getOrderId());
        if (!nullableTxn.isPresent()) {
            Shifts.fatal(StatusCode.ORDER_NOT_EXISTS);
        }
        final QueryAccountTxnResponse response = AccountTransactionCopier.INSTANCE.toQueryAccountTxnResponse(nullableTxn.get());
        response.setSuccessful(true);
        response.setCode(StatusCode.OK.code());
        response.setMessage(StatusCode.OK.message());
        return response;
    }

    @FaultBarrier
    @ApiOperation(value = "根据订单ID确认预留资源", notes = "具备fsm被动轮转能力")
    @PutMapping(value = "/confirm-transaction")
    public ConfirmAccountTxnResponse confirmTransaction(@Nonnull ConfirmAccountTxnRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        return service.confirmTransaction(request, 0);
    }

}
