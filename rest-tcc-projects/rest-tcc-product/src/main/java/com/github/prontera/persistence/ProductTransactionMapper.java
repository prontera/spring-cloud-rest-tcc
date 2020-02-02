package com.github.prontera.persistence;

import com.github.prontera.annotation.MyBatisRepository;
import com.github.prontera.domain.ProductTransaction;
import com.github.prontera.product.enums.ReservingState;
import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface ProductTransactionMapper extends CrudMapper<ProductTransaction> {

    ProductTransaction selectByOrderId(@Param("orderId") long orderId);

    int compareAndSetState(@Param("id") long id,
                           @Param("expected") ReservingState expected,
                           @Param("updating") ReservingState updating);

}
