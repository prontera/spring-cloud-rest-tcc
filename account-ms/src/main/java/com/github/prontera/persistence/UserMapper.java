package com.github.prontera.persistence;

import com.github.prontera.common.persistence.CrudMapper;
import com.github.prontera.domain.User;
import org.apache.ibatis.annotations.Param;

@SuppressWarnings("InterfaceNeverImplemented")
public interface UserMapper extends CrudMapper<User> {

    User selectByMobile(@Param("mobile") String mobile);

}