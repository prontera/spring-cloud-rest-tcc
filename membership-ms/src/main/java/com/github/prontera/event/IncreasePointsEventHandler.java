package com.github.prontera.event;

import com.github.prontera.EventHandler;
import com.github.prontera.domain.EventSubscriber;
import com.github.prontera.domain.PointFlow;
import com.github.prontera.domain.type.EventStatus;
import com.github.prontera.service.PointService;
import com.github.prontera.util.Jacksons;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.io.IOException;

/**
 * @author Zhao Junjian
 */
public class IncreasePointsEventHandler extends EventHandler {
    private final EventHandler successor;
    private final PointService pointService;
    private static final String BUSINESS_TYPE = "ADD_PTS";

    public IncreasePointsEventHandler(PointService pointService, EventHandler successor) {
        this.pointService = pointService;
        this.successor = successor;
    }

    @Override
    public void handle(EventSubscriber subscriber) {
        Preconditions.checkNotNull(subscriber);
        Preconditions.checkNotNull(subscriber.getId());
        try {
            if (Objects.equal(BUSINESS_TYPE, subscriber.getBusinessType())) {
                // 这里取巧，将生产者的报文特地写成PointFlow的格式
                final PointFlow request = Jacksons.getMapper().readValue(subscriber.getPayload(), PointFlow.class);
                // 简单地增加流水，为了简便就没有模拟任何业务上的校验
                pointService.persistFlow(request);
                // 增加总数
                pointService.increasePoint(request.getPoint(), request.getUserId());
                getMapper().updateEventStatusByPrimaryKeyInCasMode(subscriber.getId(), EventStatus.NEW, EventStatus.DONE);
            } else {
                if (successor != null) {
                    successor.handle(subscriber);
                }
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("读取JSON报文至实体时发生异常. payload: " + subscriber.getPayload() + ", entity: PointFlow.class");
        }
    }
}
