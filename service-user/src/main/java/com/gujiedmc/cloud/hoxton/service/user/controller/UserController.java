package com.gujiedmc.cloud.hoxton.service.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * user
 *
 * @author gujiedmc
 * @date 2021-03-24
 */
@Slf4j
@RestController
public class UserController {

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id) {
        log.info("查询用户信息：{}", id);
        return "UserInfo:" + id;
    }
}
