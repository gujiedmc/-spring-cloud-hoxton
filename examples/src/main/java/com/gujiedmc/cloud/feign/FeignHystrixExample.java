package com.gujiedmc.cloud.feign;

import com.gujiedmc.cloud.hoxton.common.entity.R;
import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import feign.*;
import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import rx.Observable;

import java.util.Objects;

/**
 * feign整合hystrix测试
 *
 * @author gujiedmc
 * @date 2021-04-07
 */
public class FeignHystrixExample {

    public static void main(String[] args) throws InterruptedException {

        // 封装被代理接口信息
        String url = "http://localhost:9201";
        Target.HardCodedTarget<UserService> target = new Target.HardCodedTarget<>(UserService.class, url);

        // 代理创建工厂
        UserService proxy = HystrixFeign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                // 可以指定Fallback工厂或者直接指定fallback类，工厂可以拿到异常信息。
                // .target(target,UserServiceFallback.DEFAULT)
                // .target(target, (FallbackFactory<UserService>) UserServiceFallback::new);
                .target(target, new FallbackFactory<UserService>() {
                    @Override
                    public UserService create(Throwable throwable) {
                        return new UserServiceFallback(throwable);
                    }
                });

        // 执行请求
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("gujiedmc");
        userEntity.setPassword("123456");
        userEntity.setAge(28);
        R<?> result = proxy.addUser(userEntity.getId(), userEntity);

        assert result.getCode() == 0;

        UserEntity queryResult = proxy.get(userEntity.getId());
        assert Objects.equals(userEntity, queryResult);


        Observable<UserEntity> observable = proxy.getObservable(1L);
        observable.subscribe(e -> System.out.println("Observable : " + e));

        Thread.currentThread().join();
    }

    private interface UserService {
        @RequestLine("GET /user/{id}")
        UserEntity get(@Param("id") Long id);

        @RequestLine("GET /user/{id}")
        Observable<UserEntity> getObservable(@Param("id") Long id);

        @Headers("Content-Type: application/json")
        @RequestLine("POST /user/{id}")
        R<?> addUser(@Param("id") Long id, UserEntity userEntity);
    }

    private static class UserServiceFallback implements UserService {

        private Throwable throwable;

        public UserServiceFallback(Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        public UserEntity get(Long id) {
            System.out.println("UserService#get Fallback。error:" + throwable.getMessage());
            UserEntity userEntity = new UserEntity();
            userEntity.setId(id);
            return userEntity;
        }

        @Override
        public Observable<UserEntity> getObservable(Long id) {
            System.out.println("UserService#getObservable Fallback。error:" + throwable.getMessage());
            return Observable.create(subscriber -> {
                UserEntity userEntity = new UserEntity();
                userEntity.setId(id);
                subscriber.onNext(userEntity);
                subscriber.onCompleted();
            });
        }

        @Override
        public R<?> addUser(Long id, UserEntity userEntity) {
            return R.error(throwable);
        }
    }


}
