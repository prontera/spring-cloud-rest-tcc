package com.github.prontera.http;

import com.github.prontera.annotation.FaultBarrier;
import com.github.prontera.concurrent.Pools;
import com.github.prontera.model.request.CheckoutRequest;
import com.github.prontera.model.request.DiagnoseRequest;
import com.github.prontera.model.response.CheckoutResponse;
import com.github.prontera.model.response.DiagnoseResponse;
import com.github.prontera.service.OrderService;
import com.github.prontera.util.HibernateValidators;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
@Api(tags = "Order-Debugger")
@RestController
@RequestMapping(value = "/orders", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
public class OrderController {

    private final OrderService orderService;

    @Lazy
    @Autowired
    public OrderController(@Nonnull OrderService orderService) {
        this.orderService = Objects.requireNonNull(orderService);
    }

    @FaultBarrier
    @ApiOperation(value = "结账", notes = "_")
    @PostMapping(value = "/checkout")
    public Mono<CheckoutResponse> checkout(@Nonnull @RequestBody CheckoutRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        return Mono.fromFuture(() -> orderService.checkout(request))
            .subscribeOn(Schedulers.fromExecutorService(Pools.IO));
    }

    @FaultBarrier
    @ApiOperation(value = "诊断订单", notes = "_")
    @PostMapping(value = "/diagnose")
    public Mono<DiagnoseResponse> diagnose(@Nonnull @RequestBody DiagnoseRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        return Mono.fromFuture(() -> orderService.diagnose(request))
            .subscribeOn(Schedulers.fromExecutorService(Pools.IO));
    }

}
