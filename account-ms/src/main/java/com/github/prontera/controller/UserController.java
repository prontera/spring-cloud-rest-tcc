package com.github.prontera.controller;

import com.github.prontera.model.request.LoginRequest;
import com.github.prontera.model.request.RegisterRequest;
import com.github.prontera.model.response.LoginResponse;
import com.github.prontera.model.response.RegisterResponse;
import com.github.prontera.service.UserService;
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
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户登录", notes = "获取JWT")
    @RequestMapping(value = "/users/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid @RequestBody LoginRequest request, BindingResult error) {
        return userService.login(request);
    }

    @ApiOperation(value = "用户注册", notes = "注册新用户, 余额自定义, 用于下单等一系列操作, 并可获取JWT")
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request, BindingResult error) {
        return userService.register(request);
    }

}
