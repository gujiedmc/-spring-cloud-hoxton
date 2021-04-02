package com.gujiedmc.cloud.hoxton.service.user.controller;

import com.gujiedmc.cloud.hoxton.common.entity.R;
import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.gujiedmc.cloud.hoxton.service.user.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * user
 *
 * @author gujiedmc
 * @date 2021-03-24
 */
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserDao userDao;

    @GetMapping("/{id}")
    public UserEntity getById(@PathVariable Long id) {
        log.info("查询用户信息：{}", id);
        return userDao.get(id);
    }

    @PostMapping("/{id}")
    public R<?> add(@RequestBody UserEntity userEntity) {
        userDao.save(userEntity);
        return R.ok();
    }
}
