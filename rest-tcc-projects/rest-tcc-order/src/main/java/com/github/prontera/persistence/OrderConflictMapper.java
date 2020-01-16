package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.OrderConflict;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface OrderConflictMapper extends CrudMapper<OrderConflict> {

}