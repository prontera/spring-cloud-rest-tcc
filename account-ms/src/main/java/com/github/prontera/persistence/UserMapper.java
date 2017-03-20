package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface UserMapper extends CrudMapper<User> {

    User selectByMobile(@Param("mobile") String mobile);

    List<User> selectAll(@Param("offset") int offset, @Param("limited") int limited);

    int consumeBalance(@Param("userId") Long userId, @Param("amount") Long amount);

    int returnReservedBalance(@Param("userId") Long userId, @Param("amount") Long amount);

}