package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.common.ErrorCreator;
import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import rx.Observable;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author gujiedmc
 * @date 2021-04-04
 */
public class CommandExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 同步
        HystrixCommand<UserEntity> getUserInfoCommand = new GetUserInfoCommand(1L);
        UserEntity userEntity = getUserInfoCommand.execute();
        System.out.println(userEntity);
        System.out.println("=================================================================");

        // 异步
        getUserInfoCommand = new GetUserInfoCommand(1L);
        Future<UserEntity> future = getUserInfoCommand.queue();
        System.out.println(future.get());
        System.out.println("=================================================================");

        // 转为响应式
        getUserInfoCommand = new GetUserInfoCommand(1L);
        Observable<UserEntity> observe = getUserInfoCommand.observe();
        observe.subscribe(System.out::println);
        System.out.println("=================================================================");

    }

    private static class GetUserInfoCommand extends HystrixCommand<UserEntity> {

        private Long userId;

        protected GetUserInfoCommand(Long userId) {
            super(UserRemoteService.USER_GROUP);
            this.userId = userId;
        }

        @Override
        protected UserEntity run() throws Exception {
            return UserRemoteService.USER_REMOTE_SERVICE.getUserInfo(userId);
        }
    }
}
