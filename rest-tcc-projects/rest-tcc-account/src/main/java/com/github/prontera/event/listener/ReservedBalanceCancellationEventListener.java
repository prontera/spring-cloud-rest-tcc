package com.github.prontera.event.listener;

import com.github.prontera.domain.UserBalanceTcc;
import com.github.prontera.event.ReservedBalanceCancellationEvent;
import com.github.prontera.service.tcc.UserBalanceTccService;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Zhao Junjian
 */
@Component
public class ReservedBalanceCancellationEventListener implements ApplicationListener<ReservedBalanceCancellationEvent> {

    private final UserBalanceTccService tccService;

    @Autowired
    public ReservedBalanceCancellationEventListener(UserBalanceTccService tccService) {
        this.tccService = tccService;
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onApplicationEvent(ReservedBalanceCancellationEvent event) {
        Preconditions.checkNotNull(event);
        final UserBalanceTcc res = (UserBalanceTcc) event.getSource();
        Preconditions.checkNotNull(res);
        tccService.cancelReservation(res);
    }

}
