package com.github.prontera.service;

import com.github.prontera.Shifts;
import com.github.prontera.domain.Product;
import com.github.prontera.domain.ProductTransaction;
import com.github.prontera.enums.StatusCode;
import com.github.prontera.persistence.ProductMapper;
import com.github.prontera.persistence.ProductTransactionMapper;
import com.github.prontera.product.enums.ReservingState;
import com.github.prontera.product.model.request.AddProductRequest;
import com.github.prontera.product.model.request.ConfirmProductTxnRequest;
import com.github.prontera.product.model.request.InventoryReservingRequest;
import com.github.prontera.product.model.response.AddProductResponse;
import com.github.prontera.product.model.response.ConfirmProductTxnResponse;
import com.github.prontera.product.model.response.InventoryReservingResponse;
import com.github.prontera.util.Responses;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Zhao Junjian
 * @date 2020/01/25
 */
@Service
public class ProductService {

    public static final int MAX_RETRY_CONFIRM_TIMES = 3;

    private final ProductMapper mapper;

    private final ProductTransactionMapper transactionMapper;

    private final TransactionTemplate transactionTemplate;

    @Lazy
    @Autowired
    public ProductService(@Nonnull ProductMapper mapper,
                          @Nonnull ProductTransactionMapper transactionMapper,
                          @Nonnull PlatformTransactionManager transactionManager) {
        this.mapper = Objects.requireNonNull(mapper);
        this.transactionMapper = Objects.requireNonNull(transactionMapper);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    }

    /**
     * 上单
     */
    public AddProductResponse addProduct(@Nonnull AddProductRequest request) {
        Objects.requireNonNull(request);
        // prevent from registering many times
        final String username = StringUtils.trimToEmpty(request.getName());
        final Optional<Product> nullableAccount = findByName(username);
        if (nullableAccount.isPresent()) {
            Shifts.fatal(StatusCode.PRODUCT_REGISTERED);
        }
        final Product product = new Product();
        product.setName(username);
        product.setCreateAt(LocalDateTime.now());
        product.setUpdateAt(LocalDateTime.now());
        mapper.insertSelective(product);
        return Responses.generate(AddProductResponse.class, StatusCode.OK);
    }

    /**
     * 1. 根据name检索Product
     * 2. 根据orderId检索是否已经存在ProductTransaction
     * --- a. 如果存在, 且为intermediate state, 则再次计算reservingSecond后返回, 保持try幂等
     * --- b. 如果存在, 但为final state, 则为data violation, 响应异常
     * --- c. 如果不存在, 持久化该记录
     */
    public InventoryReservingResponse reserving(@Nonnull InventoryReservingRequest request) {
        Objects.requireNonNull(request);
        // find by name
        final String name = StringUtils.trimToEmpty(request.getProductName());
        final Optional<Product> nullableAccount = findByName(name);
        if (!nullableAccount.isPresent()) {
            Shifts.fatal(StatusCode.PRODUCT_NOT_EXISTS);
        }
        final AtomicReference<InventoryReservingResponse> container = new AtomicReference<>();
        // according to order id, retrieve and check if any transaction existed
        final Long orderId = request.getOrderId();
        final Optional<ProductTransaction> nullableAccountTransaction = Optional.ofNullable(transactionMapper.selectByOrderId(orderId));
        if (nullableAccountTransaction.isPresent()) {
            final ProductTransaction accountTransaction = nullableAccountTransaction.get();
            final ReservingState reservingState = accountTransaction.getState();
            if (reservingState == ReservingState.TRYING) {
                final InventoryReservingResponse response;
                final long expiredSeconds = Math.max(0, ChronoUnit.SECONDS.between(LocalDateTime.now(), accountTransaction.getExpireAt()));
                if (expiredSeconds <= 0) {
                    // auto cancellation
                    cancellableFindTransaction(orderId);
                    Shifts.fatal(StatusCode.TIMEOUT_AND_CANCELLED);
                }
                response = Responses.generate(InventoryReservingResponse.class, StatusCode.IDEMPOTENT_RESERVING);
                response.setReservingSeconds(expiredSeconds);
                container.set(response);
            } else if (reservingState == ReservingState.INVALID) {
                Shifts.fatal(StatusCode.UNKNOWN_RESERVING_STATE);
            } else {
                Shifts.fatal(StatusCode.NON_RESERVING_STATE);
            }
        } else {
            final Product product = nullableAccount.get();
            // did not throw any exception within TransactionTemplate
            transactionTemplate.execute(status -> {
                if (deductInventory(product.getId(), request.getAmount())) {
                    final ProductTransaction accountTransaction = new ProductTransaction();
                    accountTransaction.setOrderId(orderId);
                    accountTransaction.setProductId(product.getId());
                    accountTransaction.setAmount(request.getAmount());
                    accountTransaction.setState(ReservingState.TRYING);
                    accountTransaction.setCreateAt(LocalDateTime.now());
                    accountTransaction.setUpdateAt(LocalDateTime.now());
                    accountTransaction.setExpireAt(LocalDateTime.now().plusSeconds(request.getExpectedReservingSeconds()));
                    transactionMapper.insertSelective(accountTransaction);
                    final InventoryReservingResponse response = Responses.generate(InventoryReservingResponse.class, StatusCode.OK);
                    final long expiredSeconds = Math.max(0, ChronoUnit.SECONDS.between(LocalDateTime.now(), accountTransaction.getExpireAt()));
                    response.setReservingSeconds(expiredSeconds);
                    container.set(response);
                } else {
                    container.set(Responses.generate(InventoryReservingResponse.class, StatusCode.INSUFFICIENT_INVENTORY));
                }
                return null;
            });
        }
        return container.get();
    }

    /**
     * 根据orderId检索事务记录, 若发现过期, 则自动过期并回写数据源, 并响应响应新的实体
     */
    public Optional<ProductTransaction> cancellableFindTransaction(long orderId) {
        final ProductTransaction transaction = transactionMapper.selectByOrderId(orderId);
        final AtomicReference<ProductTransaction> container = new AtomicReference<>(transaction);
        if (transaction != null) {
            final LocalDateTime now = LocalDateTime.now();
            if (transaction.getState() == ReservingState.TRYING && now.isAfter(transaction.getExpireAt())) {
                final ProductTransaction newTxn = transactionTemplate.execute(status -> {
                    transaction.setState(ReservingState.CANCELLED);
                    transaction.setDoneAt(now);
                    if (transactionMapper.compareAndSetState(transaction.getId(), ReservingState.TRYING, ReservingState.CANCELLED) <= 0) {
                        // ATTENTION: u should force to retrieve from master node in production environment.
                        return transactionMapper.selectByOrderId(orderId);
                    }
                    if (!increaseInventory(transaction.getProductId(), transaction.getAmount())) {
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
    public ConfirmProductTxnResponse confirmTransaction(@Nonnull ConfirmProductTxnRequest request, int retryTimesNow) {
        Objects.requireNonNull(request);
        // exit for fallback preventing infinity loop
        if (retryTimesNow > MAX_RETRY_CONFIRM_TIMES) {
            Shifts.fatal(StatusCode.FAIL_TO_CONFIRM);
        }
        final Long orderId = request.getOrderId();
        final Optional<ProductTransaction> nullableTxn = cancellableFindTransaction(orderId);
        if (!nullableTxn.isPresent()) {
            Shifts.fatal(StatusCode.ORDER_NOT_EXISTS);
        }
        ConfirmProductTxnResponse response = Responses.generate(ConfirmProductTxnResponse.class, StatusCode.OK);
        final ProductTransaction accountTransaction = nullableTxn.get();
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

    public Optional<Product> findByName(@Nonnull String name) {
        Objects.requireNonNull(name);
        Preconditions.checkArgument(!name.isEmpty());
        return Optional.ofNullable(mapper.selectByName(name));
    }

    boolean deductInventory(@Nonnull Long id, @Nonnull Long amount) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(amount);
        return mapper.deductInventory(id, amount) > 0;
    }

    boolean increaseInventory(@Nonnull Long id, @Nonnull Long amount) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(amount);
        return mapper.increaseInventory(id, amount) > 0;
    }

}
