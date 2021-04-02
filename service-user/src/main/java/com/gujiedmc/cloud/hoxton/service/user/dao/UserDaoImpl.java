package com.gujiedmc.cloud.hoxton.service.user.dao;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gujiedmc
 * @date 2021-04-01
 */
@Repository
public class UserDaoImpl implements UserDao {

    public Map<Long, UserEntity> data = new ConcurrentHashMap<>();

    @Override
    public void save(UserEntity userEntity) {
        data.put(userEntity.getId(), userEntity);
    }

    @Override
    public UserEntity get(Long id) {
        return data.get(id);
    }

    @Override
    public void update(UserEntity userEntity) {
        data.put(userEntity.getId(), userEntity);
    }

    @Override
    public void delete(Long id) {
        data.remove(id);
    }
}
