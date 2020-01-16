package com.github.prontera.controller.client;

import com.github.prontera.Shift;
import com.github.prontera.controller.StatusCode;
import com.github.prontera.model.User;
import com.github.prontera.model.request.BalanceReservationRequest;
import com.github.prontera.model.response.ObjectDataResponse;
import com.github.prontera.model.response.ReservationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Zhao Junjian
 */
@Component
public class AccountClientFallback implements AccountClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountClientFallback.class);

    @Override
    public ObjectDataResponse<User> findUser(@PathVariable("userId") Long userId) {
        didNotGetResponse();
        Shift.fatal(StatusCode.SERVER_IS_BUSY_NOW);
        return null;
    }

    @Override
    public ReservationResponse reserve(@RequestBody BalanceReservationRequest request) {
        didNotGetResponse();
        Shift.fatal(StatusCode.SERVER_IS_BUSY_NOW);
        return null;
    }

    private void didNotGetResponse() {
        LOGGER.error("service '{}' has become unreachable", AccountClient.SERVICE_ID);
    }

}
