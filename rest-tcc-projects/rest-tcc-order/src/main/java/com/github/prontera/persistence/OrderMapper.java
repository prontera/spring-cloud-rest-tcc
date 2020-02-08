package com.github.prontera.persistence;

import com.github.prontera.annotation.MyBatisRepository;
import com.github.prontera.domain.Order;
import com.github.prontera.enums.OrderState;
import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface OrderMapper {

    int deleteByPrimaryKey(Long id);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Order record);

    Order selectByGuid(@Param("guid") Long guid);

    int compareAndSetState(@Param("id") Long id,
                           @Param("expect") OrderState expect,
                           @Param("updating") OrderState updating);

}
