package com.eci.security.rbac.controller;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfig;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/21.
 */

@RestController
public class ApolloController {
    @ApolloConfig
    private Config config;

    @Value("${abcd:defaultabcd}")
    private String abcd;


    @GetMapping("/test")
    public String test() {
        return abcd;
    }

    @ApolloConfigChangeListener
    private void someOnChange(ConfigChangeEvent changeEvent) {
        Set<String> changes = changeEvent.changedKeys();
        changes.stream().forEach(x ->
        {
            System.out.println("changed key: " + x);
            System.out.println("changed value: " + config.getProperty(x, "default value"));
        });
    }
}
