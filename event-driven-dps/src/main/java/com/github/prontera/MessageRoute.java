package com.github.prontera;

import lombok.Value;

/**
 * @author Zhao Junjian
 */
@Value
public class MessageRoute {
    private String exchange;

    private String routeKey;
}
