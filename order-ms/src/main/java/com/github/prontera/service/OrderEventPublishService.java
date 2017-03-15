package com.github.prontera.service;

import com.github.prontera.common.persistence.CrudMapper;
import com.github.prontera.common.service.CrudServiceImpl;
import com.github.prontera.domain.OrderEventPublish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zhao Junjian
 */
@Service
public class OrderEventPublishService extends CrudServiceImpl<OrderEventPublish> {
    @Autowired
    public OrderEventPublishService(CrudMapper<OrderEventPublish> mapper) {
        super(mapper);
    }
}
