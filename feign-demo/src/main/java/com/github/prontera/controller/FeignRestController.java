package com.github.prontera.controller;

import com.github.prontera.controller.feign.PaymentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/rest/v2", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class FeignRestController {
    @Value("${server.port}")
    private String serverPort;
    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private PaymentResource paymentResource;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Map<String, ?> get() {
        //return ImmutableMap.of("feign-demo", "get");
        return paymentResource.get();
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Map<String, ?> post() {
        //return ImmutableMap.of("feign-demo", "post");
        return paymentResource.post(applicationName + ":" + serverPort);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public Map<String, ?> put() {
        //return ImmutableMap.of("feign-demo", "put");
        return paymentResource.put(applicationName + ":" + serverPort);
    }

    @RequestMapping(value = "/", method = RequestMethod.PATCH)
    public Map<String, ?> patch() {
        //return ImmutableMap.of("feign-demo", "patch");
        return paymentResource.patch(applicationName + ":" + serverPort);
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public Map<String, ?> delete() {
        //return ImmutableMap.of("feign-demo", "delete");
        return paymentResource.delete(applicationName + ":" + serverPort);
    }

    @RequestMapping(value = "/", method = RequestMethod.OPTIONS)
    public Map<String, ?> options() {
        //return ImmutableMap.of("feign-demo", "options");
        return paymentResource.options(applicationName + ":" + serverPort);
    }

    @RequestMapping(value = "/", method = RequestMethod.TRACE)
    public Map<String, ?> trace() {
        //return ImmutableMap.of("feign-demo", "trace");
        return paymentResource.trace(applicationName + ":" + serverPort);
    }
}
