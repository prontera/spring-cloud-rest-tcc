package com.github.prontera.service;

import com.github.prontera.persistence.CrudMapper;
import com.github.prontera.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Zhao Junjian
 */
@Service
public class OrderService extends CrudServiceImpl<Order> {
    @Autowired
    public OrderService(CrudMapper<Order> mapper) {
        super(mapper);
    }
}
