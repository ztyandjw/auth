package com.eci.security.rbac.util;

/**
 * @author T1m Zhang(49244143@qq.com) 2019/10/10.
 */

public class B extends  A{


    public static void main(String[] args) {
        B b = new B();
        System.out.println(b.getClass().isAssignableFrom(A.class));

    }
}
