package com.opay.algo.client.base;

/**
 * @author cxy
 */
public interface GrpcClientConfigurer {

    /**
     * facade绑定服务节点
     *
     * @param mapper
     */
    void addBinding(GrpcClientConfigurerSupport mapper);
}
