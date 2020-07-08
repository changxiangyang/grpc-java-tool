package com.opay.algo.stub;

import com.opay.algo.client.base.GrpcClientConfigurer;
import com.opay.algo.client.base.GrpcClientConfigurerSupport;
import com.opay.algo.facade.hub.HubApiFacade;
import com.opay.algo.properties.GrpcStubProperties;
import com.opay.algo.utils.SpringBeanUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import oexp.common.base.Base;
import oexp.service.hub_api.HubApiOuterClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cxy
 */
public class GrpcChannelFactory implements GrpcManagedChannel, ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = LoggerFactory.getLogger(GrpcChannelFactory.class);

    private Map<String, ManagedChannel> channelMap = new ConcurrentHashMap<>();

    private GrpcStubProperties properties;

    private GrpcChannelFactory() {
    }

    public static GrpcManagedChannel getInstance(GrpcStubProperties properties) {
        GrpcChannelFactory factory = new GrpcChannelFactory();
        factory.properties = properties;
        return factory;
    }

    @Override
    public ManagedChannel getManagedChannel(String target) {
        ManagedChannel channel = channelMap.get(target);
        if (channel != null && channel.isShutdown()) {
            channel = GrpcChannelFactory.newChannel(target);
            channelMap.put(target, channel);
        }
        return channel;
    }

    private static ManagedChannel newChannel(String target) {
        return ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();
    }

    /**
     * 初始化各服务的channel
     *
     * @param properties
     * @throws BeansException
     */
    private void initChannelMap(GrpcStubProperties properties) throws BeansException {
        if (properties != null && !CollectionUtils.isEmpty(properties.getServerNode())) {
            properties.getServerNode().forEach(p -> {
                String address = p.getHost() + ":" + p.getPort();
                System.out.println(address);
                ManagedChannel channel = newChannel(address);
                if (channel == null) {
                    throw new BeanInitializationException(String.format("init serverNode: %s failure", address));
                }
                channelMap.put(p.getName(), channel);
            });
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if (applicationContext.getParent() == null) {
            initChannelMap(this.properties);
            Map<String, GrpcClientConfigurer> configurerMap = applicationContext.getBeansOfType(GrpcClientConfigurer.class);
            if (CollectionUtils.isEmpty(configurerMap)) {
                throw new BeanInitializationException("Class 'GrpcClientConfigurer' is not implemented");
            }
            GrpcClientConfigurerSupport serverMapper = new GrpcClientConfigurerSupport();
            configurerMap.forEach((k, v) -> v.addBinding(serverMapper));
            logger.info("grpc client created.");

            HubApiFacade bean = SpringBeanUtil.getBean("hubApiFacade", HubApiFacade.class);
            logger.info("get bean:" + bean.toString());
            HubApiOuterClass.GetHubByLocReq build = HubApiOuterClass.GetHubByLocReq
                    .newBuilder()
                    .setLocation(Base.Location.newBuilder().setLat(22).setLng(114).build())
                    .setLimitMaxDist(Integer.MAX_VALUE)
                    .build();
            HubApiOuterClass.GetHubByLocRsp hubByLocRsp = bean.getHubByLoc(build);
            logger.info("rsp" + hubByLocRsp.toString());
        }
    }
}
