package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.EventSubscriber;
import com.github.prontera.domain.type.EventStatus;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface EventSubscriberMapper extends CrudMapper<EventSubscriber> {

    Set<EventSubscriber> selectLimitedEntityByEventStatus(@Param("eventStatus") EventStatus eventStatus, @Param("limited") int limited);
}