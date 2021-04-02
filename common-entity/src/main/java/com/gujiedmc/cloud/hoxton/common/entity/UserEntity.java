package com.gujiedmc.cloud.hoxton.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gujiedmc
 * @date 2021-04-01
 */
@Data
@EqualsAndHashCode
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private Integer age;
}
