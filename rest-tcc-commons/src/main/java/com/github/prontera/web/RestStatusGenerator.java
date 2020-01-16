package com.github.prontera.web;

import com.github.prontera.model.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用于生成SWAGGER文档中的全局状态码
 *
 * @author Zhao Junjian
 */
@Api(tags = "_status", description = "状态码列表")
@RestController
public class RestStatusGenerator {

    @ApiOperation(value = "状态码列表")
    @RequestMapping(value = "/status", method = RequestMethod.OPTIONS)
    public Response requireStatusCodes() {
        return null;
    }

}
