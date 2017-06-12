package com.github.prontera.service;

import com.github.prontera.domain.PointFlow;
import com.github.prontera.domain.PointSummary;
import com.github.prontera.persistence.PointFlowMapper;
import com.github.prontera.persistence.PointSummaryMapper;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Zhao Junjian
 */
@Service
public class PointService {
    @Autowired
    private PointFlowMapper flowMapper;
    @Autowired
    private PointSummaryMapper summaryMapper;

    public List<PointFlow> getAllPointFlow(Long userId) {
        Preconditions.checkNotNull(userId);
        Preconditions.checkNotNull(userId);
        return flowMapper.selectAllByUserId(userId);
    }

    public int getTotalPoints(Long userId) {
        Preconditions.checkNotNull(userId);
        Preconditions.checkNotNull(userId);
        final PointSummary summary = summaryMapper.selectByUserId(userId);
        return summary == null ? 0 : summary.getPointSum();
    }

    public int increasePoint(Integer point, Long userId) {
        Preconditions.checkNotNull(point);
        Preconditions.checkNotNull(userId);
        Preconditions.checkNotNull(point);
        Preconditions.checkNotNull(userId);
        // 如果没有则先持久化
        PointSummary summary = summaryMapper.selectByUserId(userId);
        if (summary == null) {
            summary = new PointSummary();
            summary.setPointSum(0);
            summary.setUserId(userId);
            summaryMapper.insertSelective(summary);
        }
        return summaryMapper.increasePointByUserId(point, userId);
    }

    public int persistFlow(PointFlow record) {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(record);
        return flowMapper.insertSelective(record);
    }

}
