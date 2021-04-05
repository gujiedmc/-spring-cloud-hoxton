package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;
import rx.Observer;

import static com.gujiedmc.cloud.common.ErrorCreator.randomThrowError;
import static com.gujiedmc.cloud.hystrix.UserRemoteService.USER_REMOTE_SERVICE;

/**
 * 测试observable fallback，支持多元素数据
 *
 * @author gujiedmc
 */
public class ObservableFallbackExample {

    public static void main(String[] args) {

        // 响应式command
        HystrixObservableCommand<UserEntity> observableCommand =
                new GetUserInfoObservableCommand(new Long[]{1L, 2L, 3L});

        // 响应式数据处理
        Observer<UserEntity> observer = new Observer<>() {
            @Override
            public void onCompleted() {
                System.out.println("observer completed");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("observer error");
            }

            @Override
            public void onNext(UserEntity userEntity) {
                System.out.println("observer:" + userEntity);
            }
        };
        observableCommand.observe().subscribe(observer);
    }

    private static class GetUserInfoObservableCommand extends HystrixObservableCommand<UserEntity> {

        private Long[] userIds;

        // 因为可能在处理一些数据后失败，所以需要记录已处理的数据位置
        private int index = 0;

        public GetUserInfoObservableCommand(Long[] userIds) {
            super(UserRemoteService.USER_GROUP);
            this.userIds = userIds;
        }

        /**
         * 构建响应式数据流。
         */
        @Override
        protected Observable<UserEntity> construct() {
            return Observable.create(subscriber -> {
                for (Long userId : userIds) {
                    randomThrowError("timeout:" + userId);
                    subscriber.onNext(USER_REMOTE_SERVICE.getUserInfo(userId));
                    index++;
                }
                subscriber.onCompleted();
            });
        }

        /**
         * 降级，需要处理部分已经成功的情况
         */
        @Override
        protected Observable<UserEntity> resumeWithFallback() {
            return Observable.create(subscriber -> {
                if (index + 1 < userIds.length) {
                    for (int i = index; i < userIds.length; i++) {
                        subscriber.onNext(USER_REMOTE_SERVICE.getDefaultUserInfo(userIds[i]));
                    }
                }
                subscriber.onCompleted();
            });
        }
    }
}