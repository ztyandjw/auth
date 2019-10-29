package com.eci.security.auth.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/29.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TestService {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void test() {
        System.out.println(bCryptPasswordEncoder.encode("51Cloud"));
        System.out.println(bCryptPasswordEncoder.encode("123456"));
    }

}

