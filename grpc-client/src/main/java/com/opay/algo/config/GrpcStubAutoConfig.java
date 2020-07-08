package com.opay.algo.config;


import com.opay.algo.properties.GrpcStubProperties;
import com.opay.algo.stub.GrpcManagedChannel;
import com.opay.algo.stub.GrpcChannelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cxy
 */
@Configuration
@EnableConfigurationProperties({GrpcStubProperties.class})
@ConditionalOnProperty(
        prefix = "grpc.client",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class GrpcStubAutoConfig {

    @Autowired
    private GrpcStubProperties properties;

    @Bean
    public GrpcManagedChannel createHubApiFacade() { return GrpcChannelFactory.getInstance(properties);
    }
}
