package com.github.prontera.controller;

import com.github.prontera.Delay;
import com.github.prontera.EventDrivenSubscriber;
import com.github.prontera.RandomlyThrowsException;
import com.github.prontera.config.RabbitConfiguration;
import com.github.prontera.domain.PointFlow;
import com.github.prontera.model.response.ObjectCollectionResponse;
import com.github.prontera.model.response.ObjectDataResponse;
import com.github.prontera.service.PointService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/v1", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class PointController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PointController.class);
    @Autowired
    private PointService pointService;
    @Autowired
    private EventDrivenSubscriber subscriber;

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "查询指定用户的积分总数", notes = "")
    @RequestMapping(value = "/points/sum/users/{userId}", method = RequestMethod.GET, consumes = {MediaType.ALL_VALUE})
    public ObjectDataResponse<Integer> getTotalPoints(@PathVariable Long userId) {
        final int totalPoints = pointService.getTotalPoints(userId);
        return new ObjectDataResponse<>(totalPoints);
    }

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "查询指定用户的积分流水", notes = "")
    @RequestMapping(value = "/points/flows/users/{userId}", method = RequestMethod.GET, consumes = {MediaType.ALL_VALUE})
    public ObjectCollectionResponse<PointFlow> getPointsFlow(@PathVariable Long userId) {
        final List<PointFlow> pointFlowList = pointService.getAllPointFlow(userId);
        return new ObjectCollectionResponse<>(pointFlowList);
    }

    @RabbitListener(queues = {RabbitConfiguration.POINT_QUEUE})
    public void processEvent(Map<String, Object> event) {
        LOGGER.debug("receive message: {}", event);
        subscriber.persistAndHandleMessage(event.get("business_type").toString(), event.get("payload").toString(), event.get("guid").toString());
    }
}
