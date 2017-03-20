package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.Order;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface OrderMapper extends CrudMapper<Order> {

}