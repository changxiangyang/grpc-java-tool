package com.opay.algo.client.base;

import com.opay.algo.facade.base.BaseClientFacade;
import lombok.Data;

/**
 * @author cxy
 */
@Data
public class GrpcClientConfigurerSupport {

    /**
     *
     * @param serverNode
     * @param facade
     */
    public GrpcClientConfigurerSupport binding(String serverNode, BaseClientFacade facade) {
        facade.setServerNode(serverNode);
        return this;
    }
}
