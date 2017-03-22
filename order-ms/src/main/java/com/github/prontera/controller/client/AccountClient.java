package com.github.prontera.controller.client;

import com.github.prontera.model.User;
import com.github.prontera.model.request.BalanceReservationRequest;
import com.github.prontera.model.response.ObjectDataResponse;
import com.github.prontera.model.response.ReservationResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Zhao Junjian
 */
@FeignClient(name = AccountClient.SERVICE_ID, fallback = AccountClientFallback.class)
public interface AccountClient {
    /**
     * eureka service name
     */
    String SERVICE_ID = "account";
    /**
     * common api prefix
     */
    String API_PATH = "/api/v1";

    @RequestMapping(value = API_PATH + "/users/{userId}", method = RequestMethod.GET)
    ObjectDataResponse<User> findUser(@PathVariable("userId") Long userId);

    @RequestMapping(value = API_PATH + "/balances/reservation", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    ReservationResponse reserve(@RequestBody BalanceReservationRequest request);

}
