package com.github.prontera.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author Zhao Junjian
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@RefreshScope
@ConfigurationProperties(prefix = ManualExceptionProperties.PREFIX)
public class ManualExceptionProperties {
    public static final String PREFIX = "solar.exception";

    /**
     * 是否启用随机异常
     */
    private boolean enabled;

    /**
     * 当对此数取余为0就会抛出异常
     */
    private int factor;

}
