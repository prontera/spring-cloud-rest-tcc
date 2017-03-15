package com.github.prontera.persistence;

import com.github.prontera.common.MyBatisRepository;
import com.github.prontera.common.persistence.CrudMapper;
import com.github.prontera.domain.EventProcess;
import com.github.prontera.model.type.EventProcessStatus;
import org.apache.ibatis.annotations.Param;

import java.time.OffsetDateTime;
import java.util.List;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface EventProcessMapper extends CrudMapper<EventProcess> {

    List<EventProcess> selectByEventStatus(@Param("eventStatus") EventProcessStatus eventStatus, @Param("limitCount") int limitCount);

    int updateByPrimaryKeySelectiveWithUpdateTimeOptLock(@Param("entity") EventProcess entity, @Param("oldUpdateTime") OffsetDateTime oldUpdateTime);

}