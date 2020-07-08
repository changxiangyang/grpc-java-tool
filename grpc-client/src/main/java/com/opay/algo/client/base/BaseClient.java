package com.opay.algo.client.base;

import com.opay.algo.facade.base.BaseClientFacade;
import com.opay.algo.stub.GrpcManagedChannel;
import io.grpc.ManagedChannel;
import oexp.service.hub_api.HubApiGrpc;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author cxy
 */
public class BaseClient implements BaseClientFacade {

    @Autowired
    private GrpcManagedChannel grpcManagedChannel;

    public HubApiGrpc.HubApiBlockingStub getStub() {
        ManagedChannel channel = grpcManagedChannel.getManagedChannel(getServerNode());
        return HubApiGrpc.newBlockingStub(channel);
    }

    private String serverNode;

    @Override
    public String getServerNode() {
        return serverNode;
    }

    @Override
    public void setServerNode(String serverNode) {
        this.serverNode = serverNode;
    }
}
