package com.github.prontera;

import com.github.prontera.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Zhao Junjian
 * @date 2020/01/23
 */
@Component
public class ContextStartedListener implements ApplicationRunner {

    private final ProductService service;

    @Lazy
    @Autowired
    public ContextStartedListener(@Nonnull ProductService service) {
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void run(ApplicationArguments args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        boostCp();
    }

    protected void boostCp() {
        for (int i = 0; i < 47; i++) {
            service.findByName("ps4");
        }
    }

}
