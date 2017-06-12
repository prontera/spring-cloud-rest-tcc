package com.github.prontera.persistence;

import com.github.prontera.MyBatisRepository;
import com.github.prontera.domain.EventPublisher;
import com.github.prontera.domain.type.EventStatus;
import org.apache.ibatis.annotations.Param;

import java.time.OffsetDateTime;
import java.util.Set;

@SuppressWarnings("InterfaceNeverImplemented")
@MyBatisRepository
public interface EventPublisherMapper extends CrudMapper<EventPublisher> {
    /**
     * 获取定量的指定状态的事件群，用于发往Broker
     */
    Set<EventPublisher> selectLimitedEntityByEventStatus(@Param("eventStatus") EventStatus eventStatus, @Param("limited") int limited);

    /**
     * 获取定量的、指定状态的、在指定更新时间前的事件群，用于Broker没有confirm某消息的情况下进行重新发送，目的是防止在单节点的情况因为意外宕机而导致PENDING事件没有转换为其他状态的能力。
     */
    Set<EventPublisher> selectLimitedEntityByEventStatusBeforeTheSpecifiedUpdateTime(@Param("eventStatus") EventStatus eventStatus, @Param("limited") int limited, @Param("beforeThisTime") OffsetDateTime beforeThisTime);

    /**
     * 使用乐观锁更新实体，目的防止是在多实例的情况下对同一记录重复更新的问题，该方法主要用于发往Broker前的事件状态更新所用
     */
    int updateByPrimaryKeySelectiveWithOptimisticLock(EventPublisher record);

    /**
     * 使用GUID更新实体（并没有使用乐观锁），目前仅用于处理basic.return返回NO_ROUTE状态时即时更新其状态，因为在basic.return后会调用basic.ack，
     * 而basic.ack的处理有可能会误将NO_ROUTE更新为DONE，所以并不需要乐观锁而是直接更新
     */
    int updateByGuidSelective(EventPublisher record);

    /**
     * 该方法目前主要用于处理basic.ack的状态处理，将PENDING转换为DONE或者NOT_FOUND时所用。本方法仅针对event_status的cas更新，目的同样是防止多实例的情况重复更新的问题，
     * 不适用乐观锁而是特定针对event_status的原因有：basic.ack仅会返回一次，除非重复发送相同的消息；乐观锁是针对所有字段，而本cas方法仅是针对event_status，具有更细颗粒的控制能力
     */
    int updateEventStatusByPrimaryKeyInCasMode(@Param("id") Long id, @Param("expect") EventStatus expect, @Param("target") EventStatus target);
}