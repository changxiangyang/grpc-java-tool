package com.opay.algo.server;

import com.opay.algo.annotation.GrpcService;
import com.opay.algo.annotation.GrpcServiceProxy;
import com.opay.algo.annotation.GrpcServiceTarget;
import com.opay.algo.facade.base.GrpcFacade;
import com.opay.algo.properties.GrpcServerProperties;
import com.opay.algo.util.ThreadPoolUtil;
import io.grpc.BindableService;
import lombok.Data;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author cxy
 */
@Data
public class GrpcServerStarter implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = Logger.getLogger(GrpcServerStarter.class.getName());

    private GrpcServerProperties properties;

    private GrpcServerStarter() {
    }

    public static GrpcServerStarter getInstance(GrpcServerProperties properties) {
        GrpcServerStarter starter = new GrpcServerStarter();
        starter.setProperties(properties);
        return starter;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        if (context.getParent() == null) {
            Map<String, Object> serviceProxyMap = context.getBeansWithAnnotation(GrpcServiceProxy.class);
            Map<String, Object> serviceMap = context.getBeansWithAnnotation(GrpcService.class);
            if (CollectionUtils.isEmpty(serviceProxyMap) || CollectionUtils.isEmpty(serviceMap)) {
                return;
            }
            Map<Object, Class<?>[]> serviceInterfaceMap = serviceMap.values()
                    .stream()
                    .collect(Collectors.toMap(s -> s, s -> s.getClass().getInterfaces()));
            Map<Class<?>, Object> facadeServiceMap = new HashMap<>(serviceInterfaceMap.size());
            serviceInterfaceMap.forEach((key, value) -> Arrays.stream(value)
                    .filter(GrpcFacade.class::isAssignableFrom)
                    .forEach(c -> {
                        if (!facadeServiceMap.containsKey(c)) {
                            facadeServiceMap.put(c, key);
                        } else {
                            throw new BeanCreationException("facade" + c.getName() + "finds multiple implementation classes");
                        }
                    }));
            List<BindableService> services = serviceProxyMap.values()
                    .stream()
                    .filter(b -> (b instanceof BindableService))
                    .map(b -> bandingTargetService((BindableService) b, facadeServiceMap))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            logger.info("Server start...");
            startServer(properties.getPort(), services);
        }
    }

    private void startServer(int port, List<BindableService> services) {

        ThreadPoolUtil.threadPool.execute(() -> {
            final GrpcServer server = new GrpcServer();
            try {
                server.start(port, services, ThreadPoolUtil.threadPool.getExecutor());
                server.blockUntilShutdown();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                logger.info("Server start failure:" + e.getMessage());
            }
        });

    }

    private BindableService bandingTargetService(BindableService proxyService, Map<Class<?>, Object> facadeServiceMap) {
        if (proxyService == null) {
            return null;
        }
        Class<? extends BindableService> proxyClass = proxyService.getClass();
        List<Field> targetFields = Arrays.stream(proxyClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(GrpcServiceTarget.class))
                .collect(Collectors.toList());
        long finishCount = targetFields.stream()
                .filter(f -> {
                    Class<?> fieldTypeClass = f.getType();
                    if (facadeServiceMap.containsKey(fieldTypeClass)) {
                        try {
                            f.set(proxyService, facadeServiceMap.get(fieldTypeClass));
                        } catch (IllegalAccessException e) {
                            throw new BeanCreationException("proxy" + proxyClass.getName() + "Automatic injection service:" + fieldTypeClass.getName() + " failed");
                        }
                        return true;
                    }
                    return false;
                }).count();

        return finishCount > 0 ? proxyService : null;
    }
}
