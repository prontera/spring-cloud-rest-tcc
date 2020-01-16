package com.github.prontera.controller.client;

import com.github.prontera.Shift;
import com.github.prontera.controller.StatusCode;
import com.github.prontera.model.request.TccRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Zhao Junjian
 */
@Component
public class TccClientFallback implements TccClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(TccClientFallback.class);


    @Override
    public void confirm(@RequestBody TccRequest request) {
        didNotGetResponse();
        Shift.fatal(StatusCode.SERVER_IS_BUSY_NOW);
    }

    @Override
    public void cancel(@RequestBody TccRequest request) {
        didNotGetResponse();
        Shift.fatal(StatusCode.SERVER_IS_BUSY_NOW);
    }

    private void didNotGetResponse() {
        LOGGER.error("service '{}' has become unreachable", TccClient.SERVICE_ID);
    }
}
