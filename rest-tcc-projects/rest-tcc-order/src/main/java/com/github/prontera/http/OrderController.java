package com.github.prontera.http;

import com.github.prontera.annotation.FaultBarrier;
import com.github.prontera.domain.Order;
import com.github.prontera.enums.StatusCode;
import com.github.prontera.model.request.CheckoutRequest;
import com.github.prontera.model.response.CheckoutResponse;
import com.github.prontera.service.OrderService;
import com.github.prontera.util.HibernateValidators;
import com.github.prontera.util.Responses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Zhao Junjian
 * @date 2020/01/20
 */
@Api(tags = "Order-Debugger")
@RestController
@RequestMapping(value = "/orders", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.ALL_VALUE})
public class OrderController {

    private final OrderService orderService;

    @Lazy
    @Autowired
    public OrderController(@Nonnull OrderService orderService) {
        this.orderService = Objects.requireNonNull(orderService);
    }

    @FaultBarrier
    @ApiOperation(value = "根据orderId查询详情", notes = "_")
    @GetMapping(value = "/query-order")
    public Order queryPartnerList(long orderId) {
        return orderService.find(orderId);
    }

    @FaultBarrier
    @ApiOperation(value = "结账", notes = "_")
    @PostMapping(value = "/proceed-to-checkout")
    public CheckoutResponse checkout(@Nonnull CheckoutRequest request) {
        Objects.requireNonNull(request);
        HibernateValidators.throwsIfInvalid(request);
        orderService.checkout(request);
        return Responses.generate(CheckoutResponse.class, StatusCode.OK);
    }

}
