package com.github.prontera.controller;

import com.github.prontera.common.exception.PayloadTooLongException;
import com.github.prontera.config.RabbitConfiguration;
import com.github.prontera.model.event.EventProcessPreparation;
import com.github.prontera.service.EventProcessService;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhao Junjian
 */
@Component
public class MessageQueueListener {
    @Autowired
    private EventProcessService processService;

    @Transactional(rollbackFor = Exception.class)
    @RabbitListener(queues = RabbitConfiguration.TRADE_REQUEST_QUEUE)
    public void receiveMessage(EventProcessPreparation response) {
        try {
            processService.persistMessage(response);
        } catch (PayloadTooLongException e) {
            throw new AmqpRejectAndDontRequeueException(e);
        }
    }

}
