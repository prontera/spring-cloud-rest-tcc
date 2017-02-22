package com.github.prontera.controller;

import com.github.prontera.util.Echoes;
import com.google.common.collect.ImmutableMap;
import com.netflix.discovery.EurekaClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/rest", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class EchoController {
    private static final Random RANDOM = new SecureRandom();
    @Autowired
    private Echoes echoes;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private EurekaClient eurekaClient;

    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.GET)
    public Map<String, ?> get(@PathVariable String version, HttpServletRequest request) throws IOException {
        //fallbackRandomly();
        return echoes.mark(request, null);
    }

    @HystrixCommand(fallbackMethod = "defaultFallback")
    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.POST)
    public Map<String, ?> post(@PathVariable String version, HttpServletRequest request, @RequestBody String requestBody) throws IOException {
        fallbackRandomly();
        return echoes.mark(request, requestBody);
    }

    @HystrixCommand(fallbackMethod = "defaultFallback")
    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.PUT)
    public Map<String, ?> put(@PathVariable String version, HttpServletRequest request, @RequestBody String requestBody) throws IOException {
        fallbackRandomly();
        return echoes.mark(request, requestBody);
    }

    @HystrixCommand(fallbackMethod = "defaultFallback")
    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.PATCH)
    public Map<String, ?> patch(@PathVariable String version, HttpServletRequest request, @RequestBody String requestBody) throws IOException {
        fallbackRandomly();
        return echoes.mark(request, requestBody);
    }

    @HystrixCommand(fallbackMethod = "defaultFallback")
    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.DELETE)
    public Map<String, ?> delete(@PathVariable String version, HttpServletRequest request, @RequestBody String requestBody) throws IOException {
        fallbackRandomly();
        return echoes.mark(request, requestBody);
    }

    @HystrixCommand(fallbackMethod = "defaultFallback")
    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.OPTIONS)
    public Map<String, ?> options(@PathVariable String version, HttpServletRequest request, @RequestBody String requestBody) throws IOException {
        fallbackRandomly();
        return echoes.mark(request, requestBody);
    }

    @HystrixCommand(fallbackMethod = "defaultFallback")
    @RequestMapping(value = "/{version:v[1-9]}/", method = RequestMethod.TRACE)
    public Map<String, ?> trace(@PathVariable String version, HttpServletRequest request, @RequestBody String requestBody) throws IOException {
        fallbackRandomly();
        return echoes.mark(request, requestBody);
    }

    private void fallbackRandomly() {
        if (RANDOM.nextInt() % 7 == 0) {
            throw new IllegalStateException("manual fallback");
        }
    }

    public Map<String, ?> defaultFallback(String version, HttpServletRequest request, @RequestBody String requestBody) throws IOException {
        return ImmutableMap.of("fallback", echoes.mark(request, requestBody));
    }
}
