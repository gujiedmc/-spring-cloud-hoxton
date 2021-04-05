package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.common.ErrorCreator;
import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.HystrixCommand;
import rx.Observable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 降级策略测试
 *
 * @author gujiedmc
 * @date 2021-04-04
 */
public class FallbackExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 同步
        HystrixCommand<UserEntity> getUserInfoCommand = new GetUserInfoCommand(1L);
        UserEntity userEntity = getUserInfoCommand.execute();
        System.out.println(userEntity);
    }

    private static class GetUserInfoCommand extends HystrixCommand<UserEntity> {

        private Long userId;

        protected GetUserInfoCommand(Long userId) {
            super(UserRemoteService.USER_GROUP);
            this.userId = userId;
        }

        @Override
        protected UserEntity run() throws Exception {
            ErrorCreator.randomThrowError("timeout:" + userId);
            return UserRemoteService.USER_REMOTE_SERVICE.getUserInfo(userId);
        }

        /**
         * 降级策略
         *
         * @return 返回降级处理后的数据，可以返回空数据、默认数据、残缺数据等等
         */
        @Override
        protected UserEntity getFallback() {
            return UserRemoteService.USER_REMOTE_SERVICE.getDefaultUserInfo(userId);
        }
    }


}
