package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.PointSummary;
import org.apache.ibatis.annotations.Param;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface PointSummaryMapper extends CrudMapper<PointSummary> {
    PointSummary selectByUserId(@Param("userId") Long userId);

    int increasePointByUserId(@Param("amount") int amount, @Param("userId") Long userId);
}