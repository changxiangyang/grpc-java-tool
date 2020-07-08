package com.opay.algo.stub;


import io.grpc.ManagedChannel;

/**
 * @author cxy
 */
public interface GrpcManagedChannel {

    /**
     * 获取channel
     * @param name
     * @return
     */
    ManagedChannel getManagedChannel(String name);

}
