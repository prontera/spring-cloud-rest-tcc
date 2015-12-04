package com.github.prontera.util;

import com.github.prontera.exception.IllegalValidateException;
import com.google.common.collect.ImmutableMap;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.Random;

public class Shift {
    private static final Random RAND = new Random(System.currentTimeMillis());
    private static final String CODE = "code";
    private static final String MES = "message";
    private static final String ERROR = "error";

    private Shift() {
    }

    /**
     * 将HTTP状态码写入到Map中
     *
     * @param jsonMap 用于绑定的Map集合
     * @param status  HTTP状态
     * @see HttpStatus
     */
    public static void fill(Map<String, Object> jsonMap, HttpStatus status) {
        state(jsonMap, status, null);
    }

    /**
     * 将错误绑定至Map中
     *
     * @param jsonMap 用户绑定的Map集合
     * @param status  HTTP状态码
     * @param error   错误信息
     */
    public static void state(Map<String, Object> jsonMap, HttpStatus status, String error) {
        jsonMap.put(CODE, status.value());
        if (error != null && !"".equals(error.trim())) {
            jsonMap.put(ERROR, error);
        }
    }

    public static void fill(Map<String, Object> jsonMap, HttpStatus status, Object message) {
        jsonMap.put(CODE, status.value());
        if (message != null) {
            jsonMap.put(MES, message);
        }
    }

    /**
     * 用户检验实体合法性的辅助方法, 自动向Map封装错误信息
     *
     * @param result  Spring MVC中与@Valid成对出现的BindingResult, 用于绑定错误信息
     * @param jsonMap 用户存放各类信息的Map集合
     * @throws IllegalValidateException 实体校验失败异常
     */
    public static void bindErrors(Map<String, Object> jsonMap, BindingResult result) throws IllegalValidateException {
        // 默认为true, 检测到错误时赋值为false
        boolean flag = true;
        if (result.getErrorCount() > 0) {
            flag = false;
            final ImmutableMap.Builder<String, Object> errorBuilder = ImmutableMap.builder();
            for (FieldError fieldError : result.getFieldErrors()) {
                errorBuilder.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            jsonMap.put(ERROR, errorBuilder.build());
        }
        if (!flag) {
            throw new IllegalValidateException("用户输入不符合系统设定");
        }
    }

    /**
     * 将传入的字符串两段与中间都加上空格
     *
     * @param buffer 需要转换的可变参数
     * @return
     */
    public static String sidesSpaces(String... buffer) {
        final StringBuilder builder = new StringBuilder();
        builder.append(" ");
        for (String str : buffer) {
            builder.append(str).append(" ");
        }
        return builder.toString();
    }

    /**
     * 检查排序语法是否正确, 若不为{@code asc}或者{@code desc}则抛出运行时异常IllegalArgumentException
     *
     * @param order
     * @return
     */
    public static String checkOrder(String order) {
        final boolean desc = "desc".equalsIgnoreCase(order.trim());
        final boolean asc = "asc".equalsIgnoreCase(order.trim());
        final boolean flag = (!desc && !asc);
        if (flag) {
            throw new IllegalArgumentException(order + "非法入参");
        }
        return order;
    }

    /**
     * 用户检查排序字符串拼接时, sort字符串的合法性过滤
     *
     * @param buffer   用于校验标注的数组
     * @param property 被检查的字符串
     * @return 若检查正确返回被检查的字符串
     * @throws IllegalArgumentException
     */
    public static String checkLegality(String[] buffer, String property) {
        boolean flag = false;
        for (String str : buffer) {
            if (!flag && property.equalsIgnoreCase(str)) {
                flag = true;
            }
        }
        if (!flag) {
            throw new IllegalArgumentException(property + "非法入参");
        }
        return property;
    }

}