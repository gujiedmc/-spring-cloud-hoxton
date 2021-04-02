package com.gujiedmc.cloud.hoxton.service.user.dao;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;

/**
 * @author gujiedmc
 * @date 2021-04-01
 */
public interface UserDao {

    void save(UserEntity userEntity);

    UserEntity get(Long id);

    void update(UserEntity userEntity);

    void delete(Long id);
}
