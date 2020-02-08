package com.github.prontera;

import com.github.prontera.service.AccountService;
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

    private final AccountService service;

    @Lazy
    @Autowired
    public ContextStartedListener(@Nonnull AccountService service) {
        this.service = Objects.requireNonNull(service);
    }

    @Override
    public void run(ApplicationArguments args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        boostCp();
    }

    protected void boostCp() {
        for (int i = 0; i < 7; i++) {
            service.findByUsername("chris");
        }
    }

}
