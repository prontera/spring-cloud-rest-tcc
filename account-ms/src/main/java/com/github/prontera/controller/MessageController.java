package com.github.prontera.controller;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/rest", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class MessageController {
    @Autowired
    private AmqpTemplate amqpTemplate;


}
