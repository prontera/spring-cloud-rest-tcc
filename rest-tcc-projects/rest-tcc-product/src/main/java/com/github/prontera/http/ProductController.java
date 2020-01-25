package com.github.prontera.http;

import com.github.prontera.Shifts;
import com.github.prontera.annotation.FaultBarrier;
import com.github.prontera.domain.ProductTransaction;
import com.github.prontera.enums.StatusCode;
import com.github.prontera.mapper.ProductTransactionCopier;
import com.github.prontera.product.model.request.AddProductRequest;
import com.github.prontera.product.model.request.ConfirmProductTxnRequest;
import com.github.prontera.product.model.request.InventoryReservingRequest;
import com.github.prontera.product.model.request.QueryAccountTxnRequest;
import com.github.prontera.product.model.response.AddProductResponse;
import com.github.prontera.product.model.response.ConfirmProductTxnResponse;
import com.github.prontera.product.model.response.InventoryReservingResponse;
import com.github.prontera.product.model.response.QueryProductTxnResponse;
import com.github.prontera.service.ProductService;
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
 * @author Zhao Junjian
 * @date 2020/01/25
 */
@Api(tags = "Product-Debugger")
@RestController
@RequestMapping(value = "/products", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.ALL_VALUE})
public class ProductController {

    private final ProductService service;

    @Lazy
    @Autowired
    public ProductController(@Nonnull ProductService service) {
        this.service = Objects.requireNonNull(service);
    }

    @FaultBarrier
    @ApiOperation(value = "上单", notes = "新产品默认库存为9999")
    @PostMapping(value = "/add-product")
    public AddProductResponse addProduct(@Nonnull AddProductRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        return service.addProduct(request);
    }

    @FaultBarrier
    @ApiOperation(value = "预留资源, 锁定库存", notes = "_")
    @PostMapping(value = "/reserve-inventory")
    public InventoryReservingResponse reserveInventory(@Nonnull InventoryReservingRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        return service.reserving(request);
    }

    @FaultBarrier
    @ApiOperation(value = "根据订单ID查询预留资源", notes = "如果发现预留资源过了保护期, 将自动归还资金, 具备fsm被动轮状的能力")
    @GetMapping(value = "/query-transaction")
    public QueryProductTxnResponse queryTransaction(@Nonnull QueryAccountTxnRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        final Optional<ProductTransaction> nullableTxn = service.cancellableFindTransaction(request.getOrderId());
        if (!nullableTxn.isPresent()) {
            Shifts.fatal(StatusCode.ORDER_NOT_EXISTS);
        }
        final QueryProductTxnResponse response = ProductTransactionCopier.INSTANCE.toQueryAccountTxnResponse(nullableTxn.get());
        response.setSuccessful(true);
        response.setCode(StatusCode.OK.code());
        response.setMessage(StatusCode.OK.message());
        return response;
    }

    @FaultBarrier
    @ApiOperation(value = "根据订单ID确认预留资源", notes = "具备fsm被动轮转能力")
    @PutMapping(value = "/confirm-transaction")
    public ConfirmProductTxnResponse confirmTransaction(@Nonnull ConfirmProductTxnRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        return service.confirmTransaction(request, 0);
    }

}
