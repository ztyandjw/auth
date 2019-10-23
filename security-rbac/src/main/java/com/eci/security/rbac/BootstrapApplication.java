package com.eci.security.rbac;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/9/19.
 */

@SpringBootApplication
//@EnableApolloConfig
@MapperScan(
        basePackages = {"com.eci.security.rbac.dao"}
)
public class BootstrapApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootstrapApplication.class, args);
    }

}