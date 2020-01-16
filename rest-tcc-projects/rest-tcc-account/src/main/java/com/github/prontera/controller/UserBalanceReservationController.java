package com.github.prontera.controller;

import com.github.prontera.Delay;
import com.github.prontera.RandomlyThrowsException;
import com.github.prontera.domain.UserBalanceTcc;
import com.github.prontera.model.Participant;
import com.github.prontera.model.request.BalanceReservationRequest;
import com.github.prontera.model.response.ReservationResponse;
import com.github.prontera.service.tcc.UserBalanceTccService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.OffsetDateTime;

/**
 * @author Zhao Junjian
 */
@RestController
@RequestMapping(value = UserBalanceReservationController.API_PREFIX, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
public class UserBalanceReservationController {

    private static final String RESERVATION_URI = "/balances/reservation";
    public static final String API_PREFIX = "/api/v1";

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private UserBalanceTccService tccService;

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "预留余额", notes = "")
    @RequestMapping(value = RESERVATION_URI, method = RequestMethod.POST)
    public ReservationResponse reserve(@Valid @RequestBody BalanceReservationRequest request, BindingResult error) {
        final UserBalanceTcc balanceTcc = tccService.trying(request.getUserId(), request.getAmount());
        final Long tccId = balanceTcc.getId();
        final OffsetDateTime expireTime = balanceTcc.getExpireTime();
        final Participant participant = new Participant("http://" + applicationName + API_PREFIX + RESERVATION_URI + "/" + tccId, expireTime);
        return new ReservationResponse(participant);
    }

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "确认预留余额", notes = "")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = RESERVATION_URI + "/{reservationId}", method = RequestMethod.PUT)
    public void confirm(@PathVariable Long reservationId) {
        tccService.confirmReservation(reservationId);
    }

    @Delay
    @RandomlyThrowsException
    @ApiOperation(value = "撤销预留余额", notes = "")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = RESERVATION_URI + "/{reservationId}", method = RequestMethod.DELETE)
    public void cancel(@PathVariable Long reservationId) {
        tccService.cancelReservation(reservationId);
    }

}
