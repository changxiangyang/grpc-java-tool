package com.opay.algo.facade.base;

/**
 * @author cxy
 */
public interface BaseClientFacade {

    /**
     * 获取服务节点
     * @return
     */
    String getServerNode();

    /**
     * 设置服务节点
     * @param serverNode
     */
    void setServerNode(String serverNode);
}
