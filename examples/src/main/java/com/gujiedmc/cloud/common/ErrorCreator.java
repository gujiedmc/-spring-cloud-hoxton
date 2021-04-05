package com.gujiedmc.cloud.common;

import java.util.Random;

/**
 * 创建异常的工具
 *
 * @author gujiedmc
 * @date 2021-04-05
 */
public class ErrorCreator {

    public static void randomThrowError(String msg) {
        Random random = new Random();
        int i = random.nextInt(100);
        if (i >= 50) {
            throw new RuntimeException("error:" + msg);
        }
    }
}
