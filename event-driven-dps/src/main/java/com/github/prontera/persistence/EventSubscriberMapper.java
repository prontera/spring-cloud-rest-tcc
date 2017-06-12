package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.EventSubscriber;
import com.github.prontera.domain.type.EventStatus;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface EventSubscriberMapper extends CrudMapper<EventSubscriber> {

    int updateEventStatusByPrimaryKeyInCasMode(@Param("id") Long id, @Param("expect") EventStatus expect, @Param("target") EventStatus target);

}