package com.github.prontera.event;

import com.github.prontera.domain.ProductStockTcc;
import org.springframework.context.ApplicationEvent;

/**
 * @author Zhao Junjian
 */
public class ProductStockCancellationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 8217090130282205938L;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ProductStockCancellationEvent(ProductStockTcc source) {
        super(source);
    }

}
