package com.github.prontera.service;

import com.github.prontera.exception.PartialConfirmException;
import com.github.prontera.exception.ReservationAlmostToExpireException;
import com.github.prontera.exception.ReservationExpireException;
import com.github.prontera.model.Participant;
import com.github.prontera.model.TccErrorResponse;
import com.github.prontera.model.TccRequest;
import com.github.prontera.model.TccStatus;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zhao Junjian
 */
@Service
public class CoordinateService {
    private static final int LEEWAY = 2;

    private final RestTemplate restTemplate;

    static {
        // TODO 设置rest template的media type
        final HttpHeaders header = new HttpHeaders();
        header.setAccept(ImmutableList.of(MediaType.valueOf("application/tcc")));
        final HttpEntity<Object> entity = new HttpEntity<>(header);
    }

    @Autowired
    public CoordinateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void confirm(TccRequest request) {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(request.getParticipantLinks());
        // 获取最接近过期的时间
        final OffsetDateTime theClosestToExpire = fetchTheRecentlyExpireTime(request.getParticipantLinks());
        if (theClosestToExpire.minusSeconds(LEEWAY).isBefore(OffsetDateTime.now())) {
            throw new ReservationAlmostToExpireException("there are resources be about to expire at " + theClosestToExpire);
        }
        // 调用确认资源链接
        for (Participant participant : request.getParticipantLinks()) {
            participant.setExecuteTime(OffsetDateTime.now());
            // 必须设置重试以防参与者宕机或网络抖动
            final ResponseEntity<String> response = restTemplate.exchange(participant.getUri(), HttpMethod.PUT, null, String.class);
            // 如果确认出现异常则记录
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                participant.setTccStatus(TccStatus.CONFIRMED);
            } else {
                participant.setTccStatus(TccStatus.CONFLICT);
                participant.setParticipantErrorResponse(response);
                throw new PartialConfirmException("all reservation were cancelled or timeout", new TccErrorResponse(request.getParticipantLinks()));
            }
        }
    }

    private OffsetDateTime fetchTheRecentlyExpireTime(List<Participant> participantLink) {
        Preconditions.checkNotNull(participantLink);
        // 计算出过期时间集合
        final List<OffsetDateTime> dateTimeList = participantLink.stream()
                .flatMap(x -> Stream.of(x.getExpireTime()))
                .filter(x -> x.isAfter(OffsetDateTime.now()))
                .sorted()
                .collect(Collectors.toList());
        // 检查是否具有已经过期的事务
        if (dateTimeList.size() != participantLink.size()) {
            throw new ReservationExpireException("there has a expired transaction");
        }
        // 检测是否将近过期, 集合经过Validator检查必有一个元素
        return dateTimeList.get(0);
    }

    public void cancel(TccRequest request) {
        Preconditions.checkNotNull(request);
        final List<Participant> participantList = Preconditions.checkNotNull(request.getParticipantLinks());
        for (Participant participant : participantList) {
            restTemplate.exchange(participant.getUri(), HttpMethod.DELETE, null, String.class);
        }
    }

}
