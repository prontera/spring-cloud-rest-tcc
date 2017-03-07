package com.github.prontera.controller.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author Zhao Junjian
 */
@FeignClient(name = "payment", fallback = PaymentFallbackResource.class)
public interface PaymentResource {
    String API_PATH = "/api/rest";

    @RequestMapping(value = API_PATH + "/v1/", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    Map<String, ?> get();

    @RequestMapping(value = API_PATH + "/v1/", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    Map<String, ?> post(@RequestBody String requestBody);

    @RequestMapping(value = API_PATH + "/v1/", method = RequestMethod.PUT, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    Map<String, ?> put(@RequestBody String requestBody);

    @RequestMapping(value = API_PATH + "/v1/", method = RequestMethod.PATCH, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    Map<String, ?> patch(@RequestBody String requestBody);

    @RequestMapping(value = API_PATH + "/v1/", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    Map<String, ?> delete(@RequestBody String requestBody);

    @RequestMapping(value = API_PATH + "/v1/", method = RequestMethod.OPTIONS, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    Map<String, ?> options(@RequestBody String requestBody);

    @RequestMapping(value = API_PATH + "/v1/", method = RequestMethod.TRACE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    Map<String, ?> trace(@RequestBody String requestBody);

}
