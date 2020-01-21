package com.github.prontera;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

/**
 * @author Zhao Junjian
 * @date 2020/01/21
 */
@Component
public class ContextStartedListener implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

}
