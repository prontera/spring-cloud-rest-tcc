package com.github.prontera.service;

import com.github.prontera.domain.OrderConflict;
import com.github.prontera.persistence.CrudMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhao Junjian
 */
@Service
@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
public class OrderConflictService extends CrudServiceImpl<OrderConflict> {

    public OrderConflictService(CrudMapper<OrderConflict> mapper) {
        super(mapper);
    }

}
