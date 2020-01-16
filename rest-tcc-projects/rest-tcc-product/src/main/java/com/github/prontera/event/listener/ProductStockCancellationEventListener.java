package com.github.prontera.event.listener;

import com.github.prontera.domain.ProductStockTcc;
import com.github.prontera.event.ProductStockCancellationEvent;
import com.github.prontera.service.ProductStockTccService;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhao Junjian
 */
@Component
public class ProductStockCancellationEventListener implements ApplicationListener<ProductStockCancellationEvent> {

    private final ProductStockTccService tccService;

    @Autowired
    public ProductStockCancellationEventListener(ProductStockTccService tccService) {
        this.tccService = tccService;
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onApplicationEvent(ProductStockCancellationEvent event) {
        Preconditions.checkNotNull(event);
        final ProductStockTcc res = (ProductStockTcc) event.getSource();
        Preconditions.checkNotNull(res);
        tccService.cancelReservation(res);
    }

}
