package com.github.prontera.controller;

import com.github.prontera.config.WorkUnit;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/rest", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class MessageController {
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Autowired
    private RabbitProperties rabbitProperties;
    public static final Random RANDOM = new SecureRandom();

    @RabbitListener(queues = "#{rabbitConfiguration.TOPIC_QUEUE}")
    public void processBootTask(WorkUnit content) {
        //if (RANDOM.nextInt(1000) % 3 == 0) {
        //throw new IllegalStateException("随机异常");
        //}
        //throw new AmqpRejectAndDontRequeueException("随机异常");
        System.out.println(content);
    }

    @RabbitListener(queues = "#{rabbitConfiguration.TOPIC_QUEUE}", containerFactory = "myContainerFactory")
    public void processBootTask2(WorkUnit content) {
        //if (RANDOM.nextInt(1000) % 3 == 0) {
        //throw new IllegalStateException("随机异常");
        //}
        //throw new AmqpRejectAndDontRequeueException("随机异常");
        System.out.println(content);
    }

}
