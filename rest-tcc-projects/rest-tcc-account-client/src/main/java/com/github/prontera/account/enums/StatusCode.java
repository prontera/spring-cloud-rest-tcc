package com.github.prontera.account.enums;

import com.github.prontera.enums.NumericStatusCode;
import com.github.prontera.util.Capacity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhao Junjian
 * @date 2020/01/22
 */
public enum StatusCode implements NumericStatusCode {

    // 20xxx 客户端请求成功
    OK(20000, "请求成功"),

    /**
     * 接受请求, 将会异步处理
     */
    ACCEPTED(20001, "请求已受理"),

    /**
     * 重复对同一guid进行预留操作, 已保证幂等性
     */
    IDEMPOTENT_RESERVING(20002, "幂等预留"),

    // 40xxx 客户端不合法的请求
    /**
     * 不被允许的访问
     */
    BAD_REQUEST(40000, "访问被拒绝"),

    /**
     * 字段校验错误
     */
    INVALID_MODEL_FIELDS(40001, "字段校验非法"),

    /**
     * 用户不存在
     */
    USER_NOT_EXISTS(40003, "用户名不存在"),

    /**
     * 无该订单信息
     */
    ORDER_NOT_EXISTS(40004, "无相关订单信息"),

    // 成功接收请求, 但是处理失败

    /**
     * 触发限流
     */
    RATE_LIMITED(42000, "触发限流规则, 请确认当前操作所允许的QPS"),

    /**
     * Duplicate Key
     */
    DUPLICATE_KEY(42001, "操作过快, 请稍后再试"),

    /**
     * 余额不足
     */
    INSUFFICIENT_BALANCE(42002, "余额不足"),

    /**
     * 超时并取消预留资源
     */
    TIMEOUT_AND_CANCELLED(42003, "超时并取消预留资源"),

    /**
     * {@link ReservingState}已经处于final state
     */
    RESERVING_FINAL_STATE(42004, "资源已处于final state"),

    // 50xxx 服务端异常
    /**
     * 用于处理未知的服务端错误
     */
    SERVER_UNKNOWN_ERROR(50001, "服务端异常, 请稍后再试"),

    /**
     * 用于远程调用时的系统出错
     */
    SERVER_IS_BUSY_NOW(50002, "系统繁忙, 请稍后再试"),

    /**
     * 一般常见于DB连接抖动
     */
    DB_LINK_FAILURE(50003, "DB链接失败, 请稍后再试"),

    /**
     * {@link ReservingState#INVALID}
     */
    UNKNOWN_RESERVING_STATE(50004, "未知的预留资源状态"),

    /**
     * 当预留资源过期后, 回滚账户余额失败, 可由于CP异常导致
     */
    ACCOUNT_ROLLBACK_FAILURE(50006, "账户余额增加失败, 已回滚事务"),

    /**
     * cas确认资源失败
     */
    FAIL_TO_CONFIRM(50007, "确认资源失败"),

    ;

    private static final Map<Integer, StatusCode> CACHE;

    static {
        final int expectedSize = Capacity.toMapExpectedSize(values().length);
        final Map<Integer, StatusCode> builder = new HashMap<>(expectedSize);
        for (StatusCode statusCode : values()) {
            builder.put(statusCode.code(), statusCode);
        }
        CACHE = Collections.unmodifiableMap(builder);
    }

    private final int code;

    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static StatusCode of(int code) {
        final StatusCode status = CACHE.get(code);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + code + "]");
        }
        return status;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

}
