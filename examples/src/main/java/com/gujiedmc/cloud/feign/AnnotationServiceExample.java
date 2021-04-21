package com.gujiedmc.cloud.feign;

import com.gujiedmc.cloud.hoxton.common.entity.R;
import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import feign.*;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import java.util.Objects;

/**
 * 基于feign原生注解的http代理
 *
 * @author gujiedmc
 * @date 2021-04-01
 */
public class AnnotationServiceExample {

    public static void main(String[] args) {

        // 代理创建工厂
        Feign feign = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .build();

        // 封装被代理接口信息
        String url = "http://localhost:9201";
        Target.HardCodedTarget<UserService> target = new Target.HardCodedTarget<>(UserService.class, url);

        // 创建代理
        UserService proxy = feign.newInstance(target);

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
    }

    private interface UserService {
        @RequestLine("GET /user/{id}")
        UserEntity get(@Param("id") Long id);

        @Headers("Content-Type: application/json")
        @RequestLine("POST /user/{id}")
        R<?> addUser(@Param("id") Long id, UserEntity userEntity);
    }
}
