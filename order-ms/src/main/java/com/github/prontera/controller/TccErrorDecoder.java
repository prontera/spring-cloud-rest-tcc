package com.github.prontera.controller;

import com.github.prontera.exception.PartialConfirmException;
import com.github.prontera.exception.ReservationExpireException;
import com.google.common.base.Charsets;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class TccErrorDecoder implements ErrorDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TccErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new ReservationExpireException("tcc reservation expired", response);
        }
        if (response.status() == HttpStatus.CONFLICT.value()) {
            String conflictionDetails;
            try {
                conflictionDetails = IOUtils.toString(response.body().asInputStream(), Charsets.UTF_8);
            } catch (IOException e) {
                LOGGER.error("read conflict response body exception. {}", e.toString());
                conflictionDetails = "{}";
            }
            return new PartialConfirmException(conflictionDetails);
        }
        return FeignException.errorStatus(methodKey, response);
    }
}
