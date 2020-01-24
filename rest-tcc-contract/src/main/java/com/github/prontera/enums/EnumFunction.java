package com.github.prontera.enums;

import javax.annotation.Nonnull;

/**
 * 使用入参转换为指定的Enumeration类型, 类似Java 8中的{@link java.util.function.Function},
 * 但在JDK的版本兼容上有更好的体验, 所以也没标记为{@link FunctionalInterface}
 *
 * @author Zhao Junjian
 * @date 2020/01/18
 */
public interface EnumFunction<T, R extends Enum<R>> {

    /**
     * 将给定的入参应用在转换函数中
     *
     * @param source function入参
     * @return function转化后的结果
     */
    R apply(@Nonnull T source);

}
