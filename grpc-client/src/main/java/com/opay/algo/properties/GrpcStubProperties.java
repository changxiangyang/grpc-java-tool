package com.opay.algo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


/**
 * @author cxy
 */
@Data
@ConfigurationProperties(prefix = "grpc.client")
public class GrpcStubProperties {

    /**
     * 服务节点
     */
    private List<GrpcStubNodeProperties> serverNode;
}
