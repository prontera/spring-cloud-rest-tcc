package com.github.prontera.persistence;

import com.github.prontera.annotation.MyBatisRepository;
import com.github.prontera.domain.Account;
import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface AccountMapper extends CrudMapper<Account> {

    Account selectByName(@Param("username") String username);

    int deductBalance(@Param("id") Long id, @Param("amount") Long amount);

    int increaseBalance(@Param("id") Long id, @Param("amount") Long amount);

}
