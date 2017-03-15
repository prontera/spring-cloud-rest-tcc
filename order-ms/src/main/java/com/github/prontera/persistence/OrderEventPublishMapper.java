package com.github.prontera.persistence;

import com.github.prontera.common.MyBatisRepository;
import com.github.prontera.common.persistence.CrudMapper;
import com.github.prontera.domain.OrderEventPublish;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface OrderEventPublishMapper extends CrudMapper<OrderEventPublish> {

}