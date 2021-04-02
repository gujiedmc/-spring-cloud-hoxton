package com.gujiedmc.cloud.ribbon;

import com.netflix.loadbalancer.*;

import java.util.List;

/**
 * 测试ribbon
 *
 * @author gujiedmc
 * @date 2021-03-27
 */
public class RibbonExample {

    public static void main(String[] args) {

        BaseLoadBalancer loadBalancer = new BaseLoadBalancer();

        // 添加服务
        Server userServer1 = new Server("localhost", 9201);
        Server userServer2 = new Server("localhost", 9202);
        loadBalancer.addServers(List.of(userServer1, userServer2));
        // 设置负载均衡算法
        RoundRobinRule rule = new RoundRobinRule();
        rule.setLoadBalancer(loadBalancer);
        loadBalancer.setRule(rule);
        // loadBalancer.setRule(new RandomRule());

        // 设置状态检查器
        PingUrl pingUrl = new PingUrl(false,"/user/actuator/health");
        loadBalancer.setPing(pingUrl);
        loadBalancer.setPing(new DummyPing());
        loadBalancer.setPingInterval(30);

        for (int i = 0; i < 10; i++) {
            Server server = loadBalancer.chooseServer(null);
            System.out.println(server.getHostPort());
        }

    }
}
