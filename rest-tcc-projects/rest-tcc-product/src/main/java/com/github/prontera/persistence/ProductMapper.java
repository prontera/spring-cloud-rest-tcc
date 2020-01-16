package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface ProductMapper extends CrudMapper<Product> {
    List<Product> selectAll(@Param("offset") int offset, @Param("limited") int limited);

    int consumeStock(@Param("productId") Long productId);

    int returnReservedStock(@Param("productId") Long productId);
}