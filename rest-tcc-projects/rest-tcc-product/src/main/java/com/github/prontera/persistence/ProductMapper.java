package com.github.prontera.persistence;

import com.github.prontera.annotation.MyBatisRepository;
import com.github.prontera.domain.Product;
import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface ProductMapper extends CrudMapper<Product> {
    Product selectByName(@Param("name") String name);

    int deductInventory(@Param("id") Long id, @Param("amount") Long amount);

    int increaseInventory(@Param("id") Long id, @Param("amount") Long amount);
}
