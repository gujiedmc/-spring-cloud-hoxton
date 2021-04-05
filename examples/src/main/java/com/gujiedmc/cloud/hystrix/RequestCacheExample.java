package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试缓存
 *
 * @author gujiedmc
 * @date 2021-04-05
 */
public class RequestCacheExample {

    public static void main(String[] args) {
        // 开启新的context
        HystrixRequestContext hystrixRequestContext = HystrixRequestContext.initializeContext();
        try {

            // 第一次不使用缓存
            GetUserInfoUsingCacheCommand getUserInfoUsingCacheCommand1 = new GetUserInfoUsingCacheCommand(1L);
            getUserInfoUsingCacheCommand1.execute();
            assert !getUserInfoUsingCacheCommand1.isResponseFromCache();

            // 第二次使用缓存
            GetUserInfoUsingCacheCommand getUserInfoUsingCacheCommand2 = new GetUserInfoUsingCacheCommand(1L);
            getUserInfoUsingCacheCommand2.execute();
            assert getUserInfoUsingCacheCommand2.isResponseFromCache();
        } finally {
            hystrixRequestContext.shutdown();
        }
        // 第二次开启新的context，清除缓存
        hystrixRequestContext = HystrixRequestContext.initializeContext();

        try {
            // 第一次不使用缓存
            GetUserInfoUsingCacheCommand getUserInfoUsingCacheCommand1 = new GetUserInfoUsingCacheCommand(1L);
            getUserInfoUsingCacheCommand1.execute();
            assert !getUserInfoUsingCacheCommand1.isResponseFromCache();
        } finally {
            hystrixRequestContext.shutdown();
        }
    }

    @Slf4j
    private static class GetUserInfoUsingCacheCommand extends HystrixCommand<UserEntity> {

        private Long userId;

        protected GetUserInfoUsingCacheCommand(Long userId) {
            super(UserRemoteService.USER_GROUP);
            this.userId = userId;
        }

        @Override
        protected UserEntity run() {
            System.out.println("执行查询:" + userId);
            return UserRemoteService.USER_REMOTE_SERVICE.getUserInfo(userId);
        }

        @Override
        protected String getCacheKey() {
            return userId.toString();
        }
    }
}
