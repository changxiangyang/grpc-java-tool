package com.opay.algo.config;

import com.opay.algo.server.GrpcServerStarter;
import com.opay.algo.properties.GrpcServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cxy
 */
@Configuration
@EnableConfigurationProperties({GrpcServerProperties.class})
@ConditionalOnProperty(
        prefix = "grpc.server",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class GrpcServerAutoConfig {

    @Autowired
    private GrpcServerProperties properties;

    @Bean
    public GrpcServerStarter grpcServerStarter() {
        return GrpcServerStarter.getInstance(properties);
    }
}
