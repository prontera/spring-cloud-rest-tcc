package com.github.prontera.controller;

import com.github.prontera.service.CoordinateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

}
