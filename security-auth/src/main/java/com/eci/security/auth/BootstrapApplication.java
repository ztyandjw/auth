package com.eci.security.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/19.
 */

@SpringBootApplication
//@EnableApolloConfig
@EnableEurekaClient
@MapperScan(
        basePackages = {"com.eci.security.auth.dao"}
)
public class BootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapApplication.class, args);
    }

}