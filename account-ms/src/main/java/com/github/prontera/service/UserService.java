package com.github.prontera.service;

import com.github.prontera.common.service.CrudService;
import com.github.prontera.domain.User;
import com.github.prontera.model.request.LoginRequest;
import com.github.prontera.model.request.RegisterRequest;
import com.github.prontera.model.response.LoginResponse;
import com.github.prontera.model.response.RegisterResponse;

/**
 * @author Zhao Junjian
 */
public interface UserService extends CrudService<User> {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
