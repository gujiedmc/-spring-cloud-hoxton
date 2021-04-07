package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandProperties;

import static com.gujiedmc.cloud.common.Assert.assertTrue;

/**
 * 短路器测试
 *
 * @author gujiedmc
 * @date 2021-04-05
 */
public class CircuitBreakerExample {

    /**
     * 短路场景模拟。
     * 10s中最小访问10次，异常次数50%触发熔断，熔断持续时间5s。
     *
     * 1. 执行5次正常请求，此时应该看到run方法执行5次，fallback方法执行0次
     * 2. 执行5次异常请求，此时应该看到run方法执行5次，fallback方法执行5次
     * 3. 执行5次正常请求，此时应该看到run方法执行0次，fallback方法执行5次
     * 4. 休眠3秒钟
     * 5. 执行5次正常请求，此时应该看到run方法执行0次，fallback方法执行5次
     * 6. 休眠5秒钟
     * 7. 执行5次正常请求，此时应该看到run方法执行5次，fallback方法执行0次
     */
    public static void main(String[] args) throws InterruptedException {

        // 1.
        System.out.println("---------------------------------- 1 --------------------------------------");
        for (int i = 0; i < 5; i++) {
            GetUserInfoCommandUsingCircuitBreaker command = new GetUserInfoCommandUsingCircuitBreaker(1L, false);
            command.execute();
            assertTrue(!command.isResponseFromFallback());
            assertTrue(!command.isCircuitBreakerOpen());
        }
        // 2.
        System.out.println("---------------------------------- 2 --------------------------------------");
        for (int i = 0; i < 20; i++) {
            GetUserInfoCommandUsingCircuitBreaker command = new GetUserInfoCommandUsingCircuitBreaker(1L, true);
            command.execute();
            assertTrue(command.isResponseFromFallback());
            assertTrue(!command.isCircuitBreakerOpen());
        }
        // 3.
        System.out.println("---------------------------------- 3 --------------------------------------");
        for (int i = 0; i < 5; i++) {
            GetUserInfoCommandUsingCircuitBreaker command = new GetUserInfoCommandUsingCircuitBreaker(1L, false);
            command.execute();
            assertTrue(command.isResponseFromFallback());
            assertTrue(command.isCircuitBreakerOpen());
        }
        System.out.println("---------------------------------- 4 --------------------------------------");
        // 4.
        Thread.sleep(3000L);
        // 5.
        System.out.println("---------------------------------- 5 --------------------------------------");
        for (int i = 0; i < 5; i++) {
            GetUserInfoCommandUsingCircuitBreaker command = new GetUserInfoCommandUsingCircuitBreaker(1L, false);
            command.execute();
            assertTrue(command.isResponseFromFallback());
            assertTrue(command.isCircuitBreakerOpen());
        }
        // 6.
        System.out.println("---------------------------------- 6 --------------------------------------");
        Thread.sleep(5000L);
        // 7.
        System.out.println("---------------------------------- 7 --------------------------------------");
        for (int i = 0; i < 5; i++) {
            GetUserInfoCommandUsingCircuitBreaker command = new GetUserInfoCommandUsingCircuitBreaker(1L, false);
            command.execute();
            assertTrue(!command.isResponseFromFallback());
            assertTrue(!command.isCircuitBreakerOpen());
        }

        System.out.println("语言正确");
    }

    private static class GetUserInfoCommandUsingCircuitBreaker extends HystrixCommand<UserEntity> {

        private Long userId;

        private Boolean throwError;

        protected GetUserInfoCommandUsingCircuitBreaker(Long userId, Boolean throwError) {
            super(
                    Setter.withGroupKey(UserRemoteService.USER_GROUP)
                            .andCommandPropertiesDefaults(
                                    HystrixCommandProperties.Setter()
                                            .withCircuitBreakerEnabled(true)
                                            .withCircuitBreakerRequestVolumeThreshold(1)
                                            .withCircuitBreakerErrorThresholdPercentage(10)
                                            .withCircuitBreakerSleepWindowInMilliseconds(5000)

                            )
            );
            this.userId = userId;
            this.throwError = throwError;
        }

        @Override
        protected UserEntity run() throws Exception {
            System.out.println("执行查询：" + userId);
            if (throwError) {
                throw new RuntimeException("unexpected error");
            }
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
