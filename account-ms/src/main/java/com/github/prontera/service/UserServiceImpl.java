package com.github.prontera.service;

import com.github.prontera.common.Shift;
import com.github.prontera.common.service.CrudServiceImpl;
import com.github.prontera.common.util.OrikaMapper;
import com.github.prontera.domain.User;
import com.github.prontera.model.request.LoginRequest;
import com.github.prontera.model.request.RegisterRequest;
import com.github.prontera.model.response.LoginResponse;
import com.github.prontera.model.response.RegisterResponse;
import com.github.prontera.persistence.UserMapper;
import com.github.prontera.web.StatusCode;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.html.HtmlEscapers;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhao Junjian
 */
@Service
public class UserServiceImpl extends CrudServiceImpl<User> implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RegisterResponse register(RegisterRequest request) {
        Preconditions.checkNotNull(request);
        final User dbUser = find(request.getMobile());
        if (dbUser != null) {
            Shift.fatal(StatusCode.USER_EXISTS);
        }
        // 重新计算密码
        final User transientUser = OrikaMapper.map(request, User.class);
        final String salt = generateRandomPasswordSalt();
        final String loginPassword = digestWithSalt(transientUser.getLoginPwd(), salt);
        transientUser.setPwdSalt(salt);
        transientUser.setLoginPwd(loginPassword);
        // 混合盐后入库
        persistNonNullProperties(transientUser);
        return OrikaMapper.map(transientUser, RegisterResponse.class);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Preconditions.checkNotNull(request);
        final User user = find(request.getMobile());
        if (user == null) {
            Shift.fatal(StatusCode.USER_NOT_EXISTS);
        }
        // 登录用户的密码摘要
        final String requestLoginPWd = digestWithSalt(request.getLoginPwd(), user.getPwdSalt());
        if (!Objects.equal(requestLoginPWd, user.getLoginPwd())) {
            Shift.fatal(StatusCode.INVALID_CREDENTIAL);
        }
        final LoginResponse response = new LoginResponse();
        response.setMobile(user.getMobile());
        response.setBalance(user.getBalance());
        return response;
    }

    public User find(String mobile) {
        Preconditions.checkNotNull(mobile);
        User result = null;
        if (!mobile.isEmpty()) {
            final String escapeMobile = HtmlEscapers.htmlEscaper().escape(mobile);
            result = userMapper.selectByMobile(escapeMobile);
        }
        return result;
    }

    private String digestWithSalt(String content, String key) {
        String result = content;
        for (int i = 0; i < 5; i++) {
            result = DigestUtils.sha256Hex(result + key);
        }
        return result;
    }

    private String generateRandomPasswordSalt() {
        return DigestUtils.sha256Hex(String.valueOf(System.nanoTime()));
    }

}
