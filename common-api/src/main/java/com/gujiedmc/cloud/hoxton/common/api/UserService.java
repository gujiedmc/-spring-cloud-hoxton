package com.gujiedmc.cloud.hoxton.common.api;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * user service api proxy
 *
 * @author gujiedmc
 * @date 2021-03-24
 */
@FeignClient(value = "user")
public interface UserService {

    @GetMapping("/{id}")
    UserEntity get(@PathVariable(value = "id") Long id);

    @PostMapping("/{id}")
    String save(@PathVariable(value = "id") Long id, @RequestBody UserEntity userEntity);
}
