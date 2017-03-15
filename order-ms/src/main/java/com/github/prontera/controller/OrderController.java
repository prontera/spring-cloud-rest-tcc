package com.github.prontera.controller;

import com.github.prontera.common.exception.PayloadTooLongException;
import com.github.prontera.model.event.EventPublishPreparation;
import com.github.prontera.model.event.RouteMapping;
import com.github.prontera.model.type.EventType;
import com.github.prontera.service.EventPublishService;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/v1", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class OrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private EventPublishService publishService;

    @ApiOperation(value = "下订单", notes = "")
    @RequestMapping(value = "/msg", method = RequestMethod.POST)
    public void placeOrder(@RequestParam EventType type) {
        final EventPublishPreparation pojo = EventPublishPreparation
                .builder()
                .eventType(type)
                .bizType(RouteMapping.PLACE_ORDER.name())
                .payload(ImmutableMap.of("h", "jj"))
                .build();
        try {
            publishService.persistMessage(pojo);
        } catch (PayloadTooLongException e) {
            LOGGER.error("payload长度超出限制: {}", e.getMessage());
        }
    }

}
