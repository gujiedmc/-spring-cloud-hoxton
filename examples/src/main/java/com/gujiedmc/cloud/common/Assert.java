package com.gujiedmc.cloud.common;

/**
 * @author gujiedmc
 * @date 2021-04-06
 */
public class Assert {

    public static void assertTrue(boolean flag) {
        if (!flag) {
            throw new RuntimeException("assert true fail");
        }
    }
}
