package com.github.prontera.controller.client;

import com.github.prontera.Shift;
import com.github.prontera.controller.StatusCode;
import com.github.prontera.model.Product;
import com.github.prontera.model.request.StockReservationRequest;
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
public class ProductClientFallback implements ProductClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductClientFallback.class);

    @Override
    public ObjectDataResponse<Product> findProduct(@PathVariable("id") Long productId) {
        didNotGetResponse();
        Shift.fatal(StatusCode.SERVER_IS_BUSY_NOW);
        return null;
    }

    @Override
    public ReservationResponse reserve(@RequestBody StockReservationRequest request) {
        didNotGetResponse();
        Shift.fatal(StatusCode.SERVER_IS_BUSY_NOW);
        return null;
    }

    private void didNotGetResponse() {
        LOGGER.error("service '{}' has become unreachable", ProductClient.SERVICE_ID);
    }
}
