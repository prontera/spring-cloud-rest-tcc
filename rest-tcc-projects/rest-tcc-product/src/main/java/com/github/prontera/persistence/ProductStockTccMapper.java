package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.ProductStockTcc;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface ProductStockTccMapper extends CrudMapper<ProductStockTcc> {

    Set<ProductStockTcc> selectExpireReservation(@Param("limitation") int limitation);

    int deleteTryingById(@Param("id") Long id);

    int updateToConfirmationById(@Param("id") Long id);
}