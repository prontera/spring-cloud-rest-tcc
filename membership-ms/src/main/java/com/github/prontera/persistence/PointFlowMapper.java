package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.PointFlow;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface PointFlowMapper extends CrudMapper<PointFlow> {

    List<PointFlow> selectAllByUserId(@Param("userId") Long userId);

    PointFlow selectByOrderId(@Param("orderId") Long orderId);

}