package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.EventSubscriber;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface EventSubscriberMapper extends CrudMapper<EventSubscriber> {

}