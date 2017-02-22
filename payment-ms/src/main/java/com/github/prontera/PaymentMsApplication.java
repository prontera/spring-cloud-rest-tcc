package com.github.prontera;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

@SpringCloudApplication
public class PaymentMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentMsApplication.class, args);
    }
}
