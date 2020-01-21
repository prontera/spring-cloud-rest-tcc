package com.github.prontera.service;

import com.github.prontera.domain.Order;
import com.github.prontera.enums.OrchestrationVersion;
import com.github.prontera.enums.OrderState;
import com.github.prontera.mapper.OrderRequestCopier;
import com.github.prontera.model.request.CheckoutRequest;
import com.github.prontera.persistence.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Zhao Junjian
 * @date 2020/01/21
 */
@Service
public class OrderService extends IdenticalCrudService<Order> {

    private final OrderMapper mapper;

    @Lazy
    @Autowired
    public OrderService(OrderMapper mapper) {
        super(mapper);
        this.mapper = mapper;
    }

    public int checkout(@Nonnull CheckoutRequest request) {
        Objects.requireNonNull(request);
        final Order order = OrderRequestCopier.INSTANCE.toDomainModel(request);
        // TODO
        order.setExpireAt(LocalDateTime.now());
        order.setState(OrderState.PENDING);
        order.setVersion(OrchestrationVersion.V1);
        order.setVirtualPartition(1);
        return super.persistNonNullProperties(order);
    }

}
