package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.common.ErrorCreator;
import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.HystrixCommand;

/**
 * 内嵌command做到降级方案
 *
 * @author gujiedmc
 * @date 2021-04-06
 */
public class EmbeddedCommandExample {

    public static void main(String[] args) {
        new GetUserInfoCommand(1L).execute();
    }

    /**
     * 一级方案
     */
    private static class GetUserInfoCommand extends HystrixCommand<UserEntity> {

        private Long userId;

        protected GetUserInfoCommand(Long userId) {
            super(UserRemoteService.USER_GROUP);
            this.userId = userId;
        }

        @Override
        protected UserEntity getFallback() {
            return new GetUserInfoCommandBackup(userId).execute();
        }

        @Override
        protected UserEntity run() throws Exception {
            System.out.println("一级方案执行");
            ErrorCreator.randomThrowError("timeout");
            return UserRemoteService.USER_REMOTE_SERVICE.getUserInfo(userId);
        }
    }

    /**
     * 二级方案
     */
    private static class GetUserInfoCommandBackup extends HystrixCommand<UserEntity> {

        private Long userId;

        protected GetUserInfoCommandBackup(Long userId) {
            super(UserRemoteService.USER_GROUP);
            this.userId = userId;
        }

        /**
         * 最终的降级方案
         */
        @Override
        protected UserEntity getFallback() {
            System.out.println("二级方案执行失败，执行二级降级");
            return UserRemoteService.USER_REMOTE_SERVICE.getDefaultUserInfo(userId);
        }

        @Override
        protected UserEntity run() throws Exception {
            System.out.println("二级方案执行");
            ErrorCreator.randomThrowError("timeout");
            return UserRemoteService.USER_REMOTE_SERVICE.getUserInfo(userId);
        }
    }
}
