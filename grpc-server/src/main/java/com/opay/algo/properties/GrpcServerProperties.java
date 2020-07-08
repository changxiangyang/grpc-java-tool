package com.opay.algo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cxy
 */
@Data
@ConfigurationProperties(prefix = "grpc.server")
public class GrpcServerProperties {

    private int port;
}
