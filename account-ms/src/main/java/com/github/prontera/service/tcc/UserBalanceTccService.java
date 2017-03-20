package com.github.prontera.service.tcc;

import com.github.prontera.Shift;
import com.github.prontera.controller.StatusCode;
import com.github.prontera.domain.User;
import com.github.prontera.domain.UserBalanceTcc;
import com.github.prontera.domain.type.TccStatus;
import com.github.prontera.event.ReservedBalanceCancellationEvent;
import com.github.prontera.exception.ReservationExpireException;
import com.github.prontera.persistence.CrudMapper;
import com.github.prontera.persistence.UserBalanceTccMapper;
import com.github.prontera.persistence.UserMapper;
import com.github.prontera.service.CrudServiceImpl;
import com.github.prontera.service.UserService;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

/**
 * @author Zhao Junjian
 */
@Service
public class UserBalanceTccService extends CrudServiceImpl<UserBalanceTcc> implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserBalanceTccService.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserBalanceTccMapper balanceTccMapper;
    // Autowired
    private ApplicationContext context;

    @Autowired
    public UserBalanceTccService(CrudMapper<UserBalanceTcc> mapper) {
        super(mapper);
    }

    public UserBalanceTcc trying(long userId, long amount) {
        return trying(userId, amount, 15);
    }

    public UserBalanceTcc trying(long userId, long amount, long expireSeconds) {
        Preconditions.checkArgument(userId > 0);
        Preconditions.checkArgument(amount > 0);
        Preconditions.checkArgument(expireSeconds > 0);
        final User user = userService.find(userId);
        if (user == null) {
            Shift.fatal(StatusCode.USER_NOT_EXISTS);
        }
        return trying(user, amount, expireSeconds);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserBalanceTcc trying(User user, long amount, long expireSeconds) {
        Preconditions.checkNotNull(user);
        Preconditions.checkNotNull(user.getId());
        Preconditions.checkArgument(amount > 0);
        final int isLock = userMapper.consumeBalance(user.getId(), amount);
        if (isLock == 0) {
            Shift.fatal(StatusCode.INSUFFICIENT_BALANCE);
        }
        final UserBalanceTcc tcc = new UserBalanceTcc();
        tcc.setAmount(amount);
        tcc.setStatus(TccStatus.TRY);
        tcc.setUserId(user.getId());
        tcc.setExpireTime(OffsetDateTime.now().plusSeconds(expireSeconds));
        persistNonNullProperties(tcc);
        return tcc;
    }

    /**
     * 本资源回收策略为定时轮询数据库, 存在资源竞争与重复计算的嫌疑, 待后续版本优化
     */
    @Scheduled(fixedRate = 1000)
    public void autoCancelTrying() {
        // 获取过期的资源
        final Set<UserBalanceTcc> reservations = balanceTccMapper.selectExpireReservation(100);
        for (UserBalanceTcc res : reservations) {
            context.publishEvent(new ReservedBalanceCancellationEvent(res));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long id) {
        Preconditions.checkNotNull(id);
        final UserBalanceTcc balanceTcc = find(id);
        // 无法获取说明不存在或者是已经被补偿
        if (balanceTcc == null) {
            throw new ReservationExpireException("resource " + id + " has been cancelled or does not exist at all");
        }
        cancelReservation(balanceTcc);
    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(UserBalanceTcc res) {
        Preconditions.checkNotNull(res);
        Preconditions.checkNotNull(res.getId());
        Preconditions.checkNotNull(res.getAmount());
        Preconditions.checkNotNull(res.getUserId());
        Preconditions.checkNotNull(res.getStatus());
        // 只能补偿在TRY阶段
        if (res.getStatus() == TccStatus.TRY) {
            // 依赖行锁, 必须开启事务
            final int isSucceedInDeleting = balanceTccMapper.deleteTryingById(res.getId());
            // 删除成功后才能进行补偿
            if (isSucceedInDeleting == 1) {
                final int isSuccessful = userMapper.returnReservedBalance(res.getUserId(), res.getAmount());
                if (isSuccessful == 0) {
                    throw new IllegalStateException("balance reservation id " + res.getId() + " was succeeded in deleting, but failed to make compensation for user id " + res.getUserId());
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void confirmReservation(Long id) {
        Preconditions.checkNotNull(id);
        final UserBalanceTcc balanceTcc = find(id);
        // 无法获取说明不存在或者是已经被补偿
        if (balanceTcc == null) {
            throw new ReservationExpireException("resource " + id + " has been cancelled or does not exist at all");
        }
        // 如果为Try阶段则进行确认
        if (balanceTcc.getStatus() == TccStatus.TRY) {
            final int isSuccessful = balanceTccMapper.updateToConfirmationById(id);
            // 如果返回0则说明已经被撤销
            if (isSuccessful == 0) {
                throw new ReservationExpireException("resource " + id + " has been cancelled");
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
