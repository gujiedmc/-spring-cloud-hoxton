package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import static com.gujiedmc.cloud.common.Assert.assertTrue;

/**
 * 测试超时降级。
 * 关闭熔断。
 * 设置超时时间为1s，执行代码延迟2s，执行20次，应该看到Fallback方法执行20次
 *
 * @author gujiedmc
 * @date 2021-04-06
 */
public class TimeoutExample {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            Long userId = Long.valueOf(i);
            new Thread(() -> {
                GetUserInfoCommandWithTimeout command = new GetUserInfoCommandWithTimeout(userId);
                command.execute();
                assertTrue(command.isResponseFromFallback());
            }).start();
        }
        Thread.currentThread().join();
    }

    private static class GetUserInfoCommandWithTimeout extends HystrixCommand<UserEntity> {

        private Long userId;

        /**
         * 参数设置demo，以下参数设置均为默认值
         */
        protected GetUserInfoCommandWithTimeout(Long userId) {
            super(
                    Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(UserRemoteService.SERVICE_NAME))
                            .andCommandPropertiesDefaults(
                                    HystrixCommandProperties.Setter()
                                            .withCircuitBreakerEnabled(false)
                                            .withExecutionTimeoutInMilliseconds(1000)
                            )
                            // 线程池配置
                            .andThreadPoolPropertiesDefaults(
                                    HystrixThreadPoolProperties.Setter()
                                            // 核心线程数或核心通过数，默认为10
                                            .withCoreSize(10)
                            )
            );
            this.userId = userId;
        }

        @Override
        protected UserEntity run() throws Exception {
            System.out.println("执行查询：" + userId);
            Thread.sleep(2000L);
            return UserRemoteService.USER_REMOTE_SERVICE.getUserInfo(userId);
        }

        /**
         * 降级策略
         *
         * @return 返回降级处理后的数据，可以返回空数据、默认数据、残缺数据等等
         */
        @Override
        protected UserEntity getFallback() {
            System.out.println("执行Fallback：" + userId);
            return UserRemoteService.USER_REMOTE_SERVICE.getDefaultUserInfo(userId);
        }
    }
}
