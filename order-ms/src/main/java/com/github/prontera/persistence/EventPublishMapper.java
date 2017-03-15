package com.github.prontera.persistence;

import com.github.prontera.common.MyBatisRepository;
import com.github.prontera.common.persistence.CrudMapper;
import com.github.prontera.domain.EventPublish;
import com.github.prontera.model.type.EventPublishStatus;
import org.apache.ibatis.annotations.Param;

import java.time.OffsetDateTime;
import java.util.List;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface EventPublishMapper extends CrudMapper<EventPublish> {

    EventPublish selectByPublishGuid(@Param("publishGuid") String publishGuid);

    List<EventPublish> selectByEventStatus(@Param("eventStatus") EventPublishStatus eventStatus, @Param("limitCount") int limitCount);

    int updateByPrimaryKeySelectiveWithUpdateTimeOptLock(@Param("entity") EventPublish entity, @Param("oldUpdateTime") OffsetDateTime oldUpdateTime);

}