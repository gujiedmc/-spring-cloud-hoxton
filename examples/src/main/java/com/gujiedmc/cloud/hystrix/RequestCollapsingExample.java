package com.gujiedmc.cloud.hystrix;

import com.gujiedmc.cloud.hoxton.common.entity.UserEntity;
import com.netflix.hystrix.*;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 请求合并测试
 *
 * @author gujiedmc
 * @date 2021-04-06
 */
public class RequestCollapsingExample {

    public static void main(String[] args) throws InterruptedException {
        HystrixRequestContext hystrixRequestContext = HystrixRequestContext.initializeContext();
        try {
            for (int i = 0; i < 20; i++) {
                Long userId = Long.valueOf(i);
                new GetUserInfoCollapser(userId).queue();
            }
            Thread.currentThread().join();
        }finally {
            hystrixRequestContext.shutdown();
        }
    }

    /**
     * 请求聚合器
     */
    private static class GetUserInfoCollapser extends HystrixCollapser<List<UserEntity>, UserEntity, Long> {

        public Long userId;

        public GetUserInfoCollapser(Long userId) {
            super(
                    Setter.withCollapserKey(
                            HystrixCollapserKey.Factory.asKey("getUserInfo")
                    )
                            // 合并级别，request和global
                            .andScope(Scope.REQUEST)
                            // 参数配置
                            .andCollapserPropertiesDefaults(
                                    HystrixCollapserProperties.Setter()
                                            // 是否使用缓存，默认为true
                                            .withRequestCacheEnabled(true)
                                            // 合并最大数量，默认为Integer.MAX_VALUE
                                            .withMaxRequestsInBatch(10)
                                            // 最大延迟，默认为10
                                            .withTimerDelayInMilliseconds(20)
                            )
            );
            this.userId = userId;
        }

        @Override
        public Long getRequestArgument() {
            return userId;
        }

        /**
         * 基于批量的请求参数，封装一个用于批量请求的command
         */
        @Override
        protected HystrixCommand<List<UserEntity>> createCommand(Collection<CollapsedRequest<UserEntity, Long>> collapsedRequests) {
            Long[] userIds = collapsedRequests.stream()
                    .map(CollapsedRequest::getArgument)
                    .toArray(Long[]::new);

            return new GetUserInfoCommand(userIds);
        }

        /**
         * 将批量请求的command结果，拆分到每个request中
         */
        @Override
        protected void mapResponseToRequests(List<UserEntity> batchResponse, Collection<CollapsedRequest<UserEntity, Long>> collapsedRequests) {
            int count = 0;
            for (CollapsedRequest<UserEntity, Long> collapsedRequest : collapsedRequests) {
                collapsedRequest.setResponse(batchResponse.get(count++));
            }
        }
    }

    private static class GetUserInfoCommand extends HystrixCommand<List<UserEntity>> {

        private final Long[] userIds;

        /**
         * 参数设置demo，以下参数设置均为默认值
         */
        protected GetUserInfoCommand(Long[] userIds) {
            super(
                    Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(UserRemoteService.SERVICE_NAME))
            );
            this.userIds = userIds;
        }

        @Override
        protected List<UserEntity> run() throws Exception {
            System.out.println("执行查询：" + Arrays.toString(userIds));
            return UserRemoteService.USER_REMOTE_SERVICE.getUserInfoBatch(userIds);
        }

        /**
         * 降级策略
         *
         * @return 返回降级处理后的数据，可以返回空数据、默认数据、残缺数据等等
         */
        @Override
        protected List<UserEntity> getFallback() {
            System.out.println("执行Fallback：" + Arrays.toString(userIds));
            return UserRemoteService.USER_REMOTE_SERVICE.getDefaultUserInfoBatch(userIds);
        }
    }
}
