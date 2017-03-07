package com.github.prontera.controller.feign;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author Zhao Junjian
 */
@Component
public class PaymentFallbackResource implements PaymentResource {

    @Override
    public Map<String, ?> get() {
        return ImmutableMap.of("fallback", "feign");
    }

    @Override
    public Map<String, ?> post(@RequestBody String requestBody) {
        return ImmutableMap.of("fallback", "feign");
    }

    @Override
    public Map<String, ?> put(@RequestBody String requestBody) {
        return ImmutableMap.of("fallback", "feign");
    }

    @Override
    public Map<String, ?> patch(@RequestBody String requestBody) {
        return ImmutableMap.of("fallback", "feign");
    }

    @Override
    public Map<String, ?> delete(@RequestBody String requestBody) {
        return ImmutableMap.of("fallback", "feign");
    }

    @Override
    public Map<String, ?> options(@RequestBody String requestBody) {
        return ImmutableMap.of("fallback", "feign");
    }

    @Override
    public Map<String, ?> trace(@RequestBody String requestBody) {
        return ImmutableMap.of("fallback", "feign");
    }
}
