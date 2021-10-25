package com.pigdroid.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication(scanBasePackages = {
		"com.pigdroid.hub",
		"com.pigdroid.social"
})
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "com.pigdroid.social",
        "com.pigdroid.hub"
})
@EntityScan(basePackages = {
        "com.pigdroid.social",
        "com.pigdroid.hub"
})
public class HubApplication {

    public static void main(String[] args) {
    	SpringApplication.run(HubApplication.class, args);
    }

}
