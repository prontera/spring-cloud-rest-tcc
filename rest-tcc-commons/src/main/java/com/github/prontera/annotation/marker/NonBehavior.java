package com.github.prontera.annotation.marker;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * 用于标记日志型字段, 不会参与到其他逻辑当中
 *
 * @author Zhao Junjian
 * @date 2020/02/01
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NonBehavior {
}
