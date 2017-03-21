package com.github.prontera.controller;

import com.github.prontera.model.Participant;
import com.github.prontera.model.TccRequest;
import com.github.prontera.service.CoordinateService;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = "/api/v1")
public class TestController {

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private CoordinateService service;

    private static final String TEST_URI_PREFIX = "/test";

    @ApiOperation(value = "返回204", notes = "")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = TEST_URI_PREFIX + "/204", method = RequestMethod.PUT)
    public void _204() {
        System.out.println("hello 204");
    }

    @ApiOperation(value = "返回404", notes = "")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(value = TEST_URI_PREFIX + "/404", method = RequestMethod.PUT)
    public void _404() {
        System.out.println("hello 404");
    }

    @ApiOperation(value = "返回409", notes = "")
    @ResponseStatus(HttpStatus.CONFLICT)
    @RequestMapping(value = TEST_URI_PREFIX + "/409", method = RequestMethod.PUT)
    public void _409() {
        System.out.println("hello 409");
    }

    @ApiOperation(value = "测试tcc", notes = "")
    @RequestMapping(value = TEST_URI_PREFIX, method = RequestMethod.GET)
    public void test() {
        final List<Participant> list = Lists.newArrayList();
        list.add(new Participant("http://" + applicationName + "/api/v1/test/204", OffsetDateTime.now().plusSeconds(15)));
        list.add(new Participant("http://" + applicationName + "/api/v1/test/204", OffsetDateTime.now().plusSeconds(1)));
        list.add(new Participant("http://" + applicationName + "/api/v1/test/404", OffsetDateTime.now().plusSeconds(15)));
        final TccRequest request = new TccRequest(list);
        service.confirm(request);
    }

}
