package com.github.prontera.service;

import com.github.prontera.Shifts;
import com.github.prontera.account.enums.ReservingState;
import com.github.prontera.account.model.request.BalanceReservingRequest;
import com.github.prontera.account.model.request.ConfirmAccountTxnRequest;
import com.github.prontera.account.model.request.SignUpRequest;
import com.github.prontera.account.model.response.BalanceReservingResponse;
import com.github.prontera.account.model.response.ConfirmAccountTxnResponse;
import com.github.prontera.account.model.response.SignUpResponse;
import com.github.prontera.domain.Account;
import com.github.prontera.domain.AccountTransaction;
import com.github.prontera.enums.StatusCode;
import com.github.prontera.persistence.AccountMapper;
import com.github.prontera.persistence.AccountTransactionMapper;
import com.github.prontera.util.Responses;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Zhao Junjian
 * @date 2020/01/22
 */
@Service
public class AccountService extends IdenticalCrudService<Account> {

    public static final int MAX_RETRY_CONFIRM_TIMES = 3;

    private final AccountMapper mapper;

    private final AccountTransactionMapper transactionMapper;

    private final TransactionTemplate transactionTemplate;

    @Lazy
    @Autowired
    public AccountService(@Nonnull AccountMapper mapper,
                          @Nonnull AccountTransactionMapper transactionMapper,
                          @Nonnull PlatformTransactionManager transactionManager) {
        super(mapper);
        this.mapper = Objects.requireNonNull(mapper);
        this.transactionMapper = Objects.requireNonNull(transactionMapper);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    }

    /**
     * 简单使用username注册, 用于开设新的测试账户
     */
    public SignUpResponse signUp(@Nonnull SignUpRequest request) {
        Objects.requireNonNull(request);
        // prevent from registering many times
        final String username = StringUtils.trimToEmpty(request.getName());
        final Optional<Account> nullableAccount = findByUsername(username);
        if (nullableAccount.isPresent()) {
            Shifts.fatal(StatusCode.USERNAME_REGISTERED);
        }
        final Account account = new Account();
        account.setName(username);
        super.persistNonNullProperties(account);
        return Responses.generate(SignUpResponse.class, StatusCode.OK);
    }

    /**
     * 1. 根据username检索Account
     * 2. 根据orderId检索是否已经存在AccountTransaction
     * --- a. 如果存在, 且为intermediate state, 则再次计算reservingSecond后返回, 保持try幂等
     * --- b. 如果存在, 但为final state, 则为data violation, 响应异常
     * --- c. 如果不存在, 持久化该记录
     */
    public BalanceReservingResponse reserving(@Nonnull BalanceReservingRequest request) {
        Objects.requireNonNull(request);
        // find by username
        final String username = StringUtils.trimToEmpty(request.getUsername());
        final Optional<Account> nullableAccount = findByUsername(username);
        if (!nullableAccount.isPresent()) {
            Shifts.fatal(StatusCode.USER_NOT_EXISTS);
        }
        final AtomicReference<BalanceReservingResponse> container = new AtomicReference<>();
        // according to order id, retrieve and check if any transaction existed
        final Long orderId = request.getOrderId();
        final Optional<AccountTransaction> nullableAccountTransaction = Optional.ofNullable(transactionMapper.selectByOrderId(orderId));
        if (nullableAccountTransaction.isPresent()) {
            final AccountTransaction accountTransaction = nullableAccountTransaction.get();
            final ReservingState reservingState = accountTransaction.getState();
            if (reservingState == ReservingState.TRYING) {
                final BalanceReservingResponse response;
                final long expiredSeconds = Math.max(0, ChronoUnit.SECONDS.between(LocalDateTime.now(), accountTransaction.getExpireAt()));
                if (expiredSeconds <= 0) {
                    // auto cancellation
                    cancellableFindTransaction(orderId);
                    Shifts.fatal(StatusCode.TIMEOUT_AND_CANCELLED);
                }
                response = Responses.generate(BalanceReservingResponse.class, StatusCode.IDEMPOTENT_RESERVING);
                response.setReservingSeconds(expiredSeconds);
                container.set(response);
            } else if (reservingState == ReservingState.INVALID) {
                Shifts.fatal(StatusCode.UNKNOWN_RESERVING_STATE);
            } else {
                Shifts.fatal(StatusCode.NON_RESERVING_STATE);
            }
        } else {
            final Account account = nullableAccount.get();
            // did not throw any exception within TransactionTemplate
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    if (deductBalance(account.getId(), request.getAmount())) {
                        final AccountTransaction accountTransaction = new AccountTransaction();
                        accountTransaction.setOrderId(orderId);
                        accountTransaction.setUserId(account.getId());
                        accountTransaction.setAmount(request.getAmount());
                        accountTransaction.setState(ReservingState.TRYING);
                        accountTransaction.setCreateAt(LocalDateTime.now());
                        accountTransaction.setUpdateAt(LocalDateTime.now());
                        accountTransaction.setExpireAt(LocalDateTime.now().plusSeconds(request.getExpectedReservingSeconds()));
                        transactionMapper.insertSelective(accountTransaction);
                        final BalanceReservingResponse response = Responses.generate(BalanceReservingResponse.class, StatusCode.OK);
                        final long expiredSeconds = Math.max(0, ChronoUnit.SECONDS.between(LocalDateTime.now(), accountTransaction.getExpireAt()));
                        response.setReservingSeconds(expiredSeconds);
                        container.set(response);
                    } else {
                        container.set(Responses.generate(BalanceReservingResponse.class, StatusCode.INSUFFICIENT_BALANCE));
                    }
                }
            });
        }
        return container.get();
    }

    /**
     * 根据orderId检索事务记录, 若发现过期, 则自动过期并回写数据源, 并响应响应新的实体
     */
    public Optional<AccountTransaction> cancellableFindTransaction(long orderId) {
        final AccountTransaction transaction = transactionMapper.selectByOrderId(orderId);
        final AtomicReference<AccountTransaction> container = new AtomicReference<>(transaction);
        if (transaction != null) {
            final LocalDateTime now = LocalDateTime.now();
            if (transaction.getState() == ReservingState.TRYING && now.isAfter(transaction.getExpireAt())) {
                final AccountTransaction newTxn = transactionTemplate.execute(status -> {
                    transaction.setState(ReservingState.CANCELLED);
                    transaction.setDoneAt(now);
                    if (transactionMapper.compareAndSetState(transaction.getId(), ReservingState.TRYING, ReservingState.CANCELLED) <= 0) {
                        // ATTENTION: u should force to retrieve from master node in production environment.
                        return transactionMapper.selectByOrderId(orderId);
                    }
                    if (!increaseBalance(transaction.getUserId(), transaction.getAmount())) {
                        Shifts.fatal(StatusCode.ACCOUNT_ROLLBACK_FAILURE);
                    }
                    return transaction;
                });
                container.set(newTxn);
            }
        }
        return Optional.ofNullable(container.get());
    }

    /**
     * 1. 使用auto-cancellation的查询方式, 根据orderId检索事务记录,
     * 2. 如果是trying状态, 则进行确认, 并响应成功
     * 3. 如果是final state
     * --- a. 如果是cancel, 则响应特定错误码
     * --- b. 如果已经为confirm, 则同样响应成功
     */
    public ConfirmAccountTxnResponse confirmTransaction(@Nonnull ConfirmAccountTxnRequest request, int retryTimesNow) {
        Objects.requireNonNull(request);
        // exit for fallback preventing infinity loop
        if (retryTimesNow > MAX_RETRY_CONFIRM_TIMES) {
            Shifts.fatal(StatusCode.FAIL_TO_CONFIRM);
        }
        final Long orderId = request.getOrderId();
        final Optional<AccountTransaction> nullableTxn = cancellableFindTransaction(orderId);
        if (!nullableTxn.isPresent()) {
            Shifts.fatal(StatusCode.ORDER_NOT_EXISTS);
        }
        ConfirmAccountTxnResponse response = Responses.generate(ConfirmAccountTxnResponse.class, StatusCode.OK);
        final AccountTransaction accountTransaction = nullableTxn.get();
        final ReservingState reservingState = accountTransaction.getState();
        if (reservingState == ReservingState.TRYING) {
            if (transactionMapper.compareAndSetState(accountTransaction.getId(), ReservingState.TRYING, ReservingState.CONFIRMED) <= 0) {
                // ATTENTION: u should force to retrieve from master node in production environment.
                return confirmTransaction(request, retryTimesNow + 1);
            }
        } else if (reservingState == ReservingState.CANCELLED) {
            Shifts.fatal(StatusCode.TIMEOUT_AND_CANCELLED);
        } else if (reservingState == ReservingState.INVALID) {
            Shifts.fatal(StatusCode.UNKNOWN_RESERVING_STATE);
        }
        return response;
    }

    public Optional<Account> findByUsername(@Nonnull String username) {
        Objects.requireNonNull(username);
        Preconditions.checkArgument(!username.isEmpty());
        return Optional.ofNullable(mapper.selectByName(username));
    }

    boolean deductBalance(@Nonnull Long id, @Nonnull Long amount) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(amount);
        return mapper.deductBalance(id, amount) > 0;
    }

    boolean increaseBalance(@Nonnull Long id, @Nonnull Long amount) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(amount);
        return mapper.increaseBalance(id, amount) > 0;
    }

}
