package com.gujiedmc.cloud.hoxton.common.api.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gujiedmc
 * @date 2021-03-24
 */
@EnableFeignClients("com.gujiedmc.cloud.hoxton.common.api")
@Configuration
public class CommonApiConfig {
}
