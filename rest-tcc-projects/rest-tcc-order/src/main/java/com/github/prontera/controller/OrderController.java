package com.github.prontera.controller;

import com.github.prontera.Delay;
import com.github.prontera.RandomlyThrowsException;
import com.github.prontera.domain.Order;
import com.github.prontera.model.request.PaymentRequest;
import com.github.prontera.model.request.PlaceOrderRequest;
import com.github.prontera.model.response.ObjectDataResponse;
import com.github.prontera.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/v1", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "下单", notes = "生成预订单")
    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public ObjectDataResponse<Order> placeOrder(@Valid @RequestBody PlaceOrderRequest request, BindingResult result) {
        return orderService.placeOrder(request);
    }

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "确认订单", notes = "支付及确认")
    @RequestMapping(value = "/orders/confirmation", method = RequestMethod.POST)
    public ObjectDataResponse<Order> payOff(@Valid @RequestBody PaymentRequest request, BindingResult result) {
        return orderService.confirm(request);
    }
}
