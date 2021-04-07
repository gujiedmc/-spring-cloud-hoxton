package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.*;

/**
 * 限流测试
 *
 * 先关闭超时设置
 * 线程池队列设置，线程数量为15，队列为10，每个请求执行3s
 * 执行30次请求，应该在线程中15个，队列中10个，5个直接走fallback
 *
 * @author gujiedmc
 * @date 2021-04-05
 */
public class RateLimitExample {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 30; i++) {
            Long userId = Long.valueOf(i);
            new Thread(() -> new GetUserInfoCommandWithRateLimit(userId).execute()).start();
        }
        Thread.currentThread().join();
    }

    private static class GetUserInfoCommandWithRateLimit extends HystrixCommand<UserEntity> {

        private Long userId;

        /**
         * 参数设置demo，以下参数设置均为默认值
         */
        protected GetUserInfoCommandWithRateLimit(Long userId) {
            super(
                    Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(UserRemoteService.SERVICE_NAME))
                            .andCommandPropertiesDefaults(
                                    HystrixCommandProperties.Setter()
                                    .withExecutionTimeoutEnabled(false)
                            )
                            // 线程池配置
                            .andThreadPoolPropertiesDefaults(
                                    HystrixThreadPoolProperties.Setter()
                                            // 核心线程数或核心通过数，默认为10
                                            .withCoreSize(15)
                                            .withMaxQueueSize(10)
                                            // 队列数量，因为上面的maxQueueSize无法动态修改，因此使用这个
                                            .withQueueSizeRejectionThreshold(10)
                            )
            );
            this.userId = userId;
        }

        @Override
        protected UserEntity run() throws Exception {
            System.out.println("执行查询：" + userId);
            Thread.sleep(3000L);
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
