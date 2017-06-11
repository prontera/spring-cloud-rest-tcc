package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.EventPublisher;
import com.github.prontera.domain.type.EventStatus;
import org.apache.ibatis.annotations.Param;

import java.time.OffsetDateTime;
import java.util.Set;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface EventPublisherMapper extends CrudMapper<EventPublisher> {
    Set<EventPublisher> selectLimitedEntityByEventStatus(@Param("eventStatus") EventStatus eventStatus, @Param("limited") int limited);

    Set<EventPublisher> selectLimitedEntityByEventStatusBeforeTheSpecifiedUpdateTime(@Param("eventStatus") EventStatus eventStatus, @Param("limited") int limited, @Param("beforeThisTime") OffsetDateTime beforeThisTime);

    int updateByPrimaryKeySelectiveWithOptimisticLock(EventPublisher record);

    int updateByGuidSelective(EventPublisher record);

    int updateEventStatusByPrimaryKeyInCasMode(@Param("id") Long id, @Param("expect") EventStatus expect, @Param("target") EventStatus target);
}