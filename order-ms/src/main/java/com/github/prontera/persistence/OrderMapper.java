package com.github.prontera.persistence;

import com.github.prontera.common.MyBatisRepository;
import com.github.prontera.common.persistence.CrudMapper;
import com.github.prontera.domain.Order;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface OrderMapper extends CrudMapper<Order> {

}