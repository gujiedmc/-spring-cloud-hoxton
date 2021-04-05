package com.gujiedmc.cloud.feign;

import com.gujiedmc.cloud.hoxton.common.entity.R;
import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import com.netflix.ribbon.RibbonRequest;
import feign.*;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.ribbon.LBClient;
import feign.ribbon.LBClientFactory;
import feign.ribbon.LoadBalancingTarget;
import feign.ribbon.RibbonClient;

import java.util.List;

/**
 * feign整合ribbon
 *
 * @author gujiedmc
 * @date 2021-04-03
 */
public class FeignRibbonExample {

    public static void main(String[] args) {

        // 创建ribbon负载君合器
        IRule rule = new RoundRobinRule();
        IPing ping = new DummyPing();
        LoadBalancerStats loadBalancerStats = new LoadBalancerStats();

        BaseLoadBalancer loadBalancer = new BaseLoadBalancer("user", rule, loadBalancerStats, ping);
        // 添加服务
        Server userServer1 = new Server("localhost", 9201);
        Server userServer2 = new Server("localhost", 9202);
        loadBalancer.addServers(List.of(userServer1, userServer2));


        // 创建 feign整合ribbon的RibbonClient
        IClientConfig config =
                ClientFactory.getNamedConfig("user", LBClientFactory.DisableAutoRetriesByDefaultClientConfig.class);
        // 因为这里测试，只有一个服务，所以工厂直接返回了user的LoadBalancer
        LBClientFactory lbClientFactory = s -> LBClient.create(loadBalancer, config);

        RibbonClient ribbonClient = RibbonClient.builder()
                .lbClientFactory(lbClientFactory)
                .build();


        // 创建feign代理对象
        Feign feign = Feign.builder()
                .client(ribbonClient)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .build();

        Target<UserService> target = new Target.HardCodedTarget<>(UserService.class, "http://user");
        UserService userService = feign.newInstance(target);

        // 执行请求
        for (int i = 0; i < 10; i++) {
            UserEntity userEntity = userService.get(1L);
            System.out.println("userEntity = " + userEntity);
        }

    }

    public interface UserService {
        @RequestLine("GET /user/{id}")
        UserEntity get(@Param("id") Long id);

        @Headers("Content-Type: application/json")
        @RequestLine("POST /user/{id}")
        R<?> addUser(@Param("id") Long id, UserEntity userEntity);
    }
}
