package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;
import rx.Observer;

import static com.gujiedmc.cloud.common.ErrorCreator.randomThrowError;
import static com.gujiedmc.cloud.hystrix.UserRemoteService.USER_REMOTE_SERVICE;

/**
 * 测试observable command，支持多元素数据
 *
 * @author gujiedmc
 */
public class ObservableCommandExample {

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
        System.out.println("=================================================================");

        // 当只有一个元素的时候可以直接转为block
        observableCommand = new GetUserInfoObservableCommand(new Long[]{1L});
        UserEntity entity = observableCommand.observe().toBlocking().single();
        System.out.println("observer:" + entity);
        System.out.println("=================================================================");
    }

    private static class GetUserInfoObservableCommand extends HystrixObservableCommand<UserEntity> {

        private Long[] userIds;

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
                    subscriber.onNext(USER_REMOTE_SERVICE.getUserInfo(userId));
                }
                subscriber.onCompleted();
            });
        }
    }
}