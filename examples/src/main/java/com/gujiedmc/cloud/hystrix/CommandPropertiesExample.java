package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.common.ErrorCreator;
import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.*;

/**
 * 参数设置demo
 *
 * @author gujiedmc
 * @date 2021-04-05
 */
public class CommandPropertiesExample {

    public static void main(String[] args) {
        new GetUserInfoCommand(1L);
    }

    private static class GetUserInfoCommand extends HystrixCommand<UserEntity> {

        private Long userId;

        /**
         * 参数设置demo，以下参数设置均为默认值
         */
        protected GetUserInfoCommand(Long userId) {
            super(
                    Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(UserRemoteService.SERVICE_NAME))
                            .andCommandKey(HystrixCommandKey.Factory.asKey(UserRemoteService.SERVICE_NAME))
                            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(UserRemoteService.SERVICE_NAME))
                            // command配置，以下参数设置均为默认值
                            .andCommandPropertiesDefaults(
                                    HystrixCommandProperties.Setter()
                                            // 基于信号量或线程池
                                            .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                            // 开启熔断
                                            .withCircuitBreakerEnabled(true)
                                            // 触发熔断的10s内最小请求数量，10s内请求数量小于20不执行熔断
                                            .withCircuitBreakerRequestVolumeThreshold(20)
                                            // 触发熔断的异常比例，10s内异常比例小于50%比执行熔断
                                            .withCircuitBreakerErrorThresholdPercentage(50)
                                            // 熔断持续时间，持续5s后进入半熔断状态
                                            .withCircuitBreakerSleepWindowInMilliseconds(5000)
                                            // 开启缓存
                                            .withRequestCacheEnabled(true)
                                            // 开启降级
                                            .withFallbackEnabled(true)
                                            // 开启超时
                                            .withExecutionTimeoutEnabled(true)
                                            // 超时时间
                                            .withExecutionTimeoutInMilliseconds(1000)

                                            // 信号量最大并发数量
                                            .withExecutionIsolationSemaphoreMaxConcurrentRequests(10)
                                            // 信号量降级最大并发数量
                                            .withFallbackIsolationSemaphoreMaxConcurrentRequests(10)

                            )
                            // 线程池配置，以下参数设置均为默认值
                            .andThreadPoolPropertiesDefaults(
                                    HystrixThreadPoolProperties.Setter()
                                            // 核心线程数或核心通过数，默认为10
                                            .withCoreSize(10)
                                            // 队列数量，-1表示关闭，直接使用SynchronousQueue
                                            .withMaxQueueSize(-1)
                                            // 是否开启动态线程数量
                                            .withAllowMaximumSizeToDivergeFromCoreSize(false)
                                            // 最大线程数量
                                            .withMaximumSize(10)
                                            // 动态线程最大空闲时长
                                            .withKeepAliveTimeMinutes(1)
                                            // 队列数量，因为上面的maxQueueSize无法动态修改，因此使用这个
                                            .withQueueSizeRejectionThreshold(5)
                            )
            );
            this.userId = userId;
        }

        @Override
        protected UserEntity run() throws Exception {
            System.out.println("执行查询：" + userId);
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
