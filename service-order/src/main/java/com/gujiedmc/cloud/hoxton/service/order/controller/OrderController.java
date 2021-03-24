package com.gujiedmc.cloud.hoxton.service.order.controller;

import com.gujiedmc.cloud.hoxton.common.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * order controller
 *
 * @author gujiedmc
 * @date 2021-03-24
 */
@Slf4j
@RestController
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id) {
        log.info("查询订单信息：{}", id);
        Long userId = 1L;
        ResponseEntity<String> response = restTemplate.getForEntity("http://user/user/" + userId, String.class);
        String userInfo = response.getBody();
        return "OrderInfo:" + id + "," + userInfo;
    }

    @GetMapping("/feign/{id}")
    public String getByIdWithFeign(@PathVariable Long id) {
        log.info("查询订单信息：{}", id);
        Long userId = 1L;
        String userInfo = userService.getById(userId);
        return "OrderInfo:" + id + "," + userInfo;
    }
}
