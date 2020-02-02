package com.github.prontera.persistence;

import com.github.prontera.account.enums.ReservingState;
import com.github.prontera.annotation.MyBatisRepository;
import com.github.prontera.domain.AccountTransaction;
import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface AccountTransactionMapper extends CrudMapper<AccountTransaction> {

    AccountTransaction selectByOrderId(@Param("orderId") long orderId);

    int compareAndSetState(@Param("id") long id,
                           @Param("expected") ReservingState expected,
                           @Param("updating") ReservingState updating);

}
