package com.gujiedmc.cloud.hoxton.common.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * user service api proxy
 *
 * @author gujiedmc
 * @date 2021-03-24
 */
@FeignClient(value = "user",path = "user")
public interface UserService {

    @GetMapping("/{id}")
    String getById(@PathVariable(value = "id") Long id);
}
