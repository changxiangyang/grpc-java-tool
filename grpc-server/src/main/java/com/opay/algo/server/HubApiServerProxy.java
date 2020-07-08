package com.opay.algo.server;

import com.opay.algo.annotation.GrpcServiceProxy;
import com.opay.algo.annotation.GrpcServiceTarget;
import com.opay.algo.facade.hub.HubApiFacade;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.Setter;
import oexp.service.hub_api.HubApiGrpc;
import oexp.service.hub_api.HubApiOuterClass;

import java.util.Optional;

/**
 * @author cxy
 */
@GrpcServiceProxy
public class HubApiServerProxy extends HubApiGrpc.HubApiImplBase {

    @Setter
    @GrpcServiceTarget
    @Getter
    public HubApiFacade target;

    @Override
    public void getHubByLoc(HubApiOuterClass.GetHubByLocReq request, StreamObserver<HubApiOuterClass.GetHubByLocRsp> responseObserver) {
        Optional.ofNullable(target).ifPresent(t -> {
            responseObserver.onNext(t.getHubByLoc(request));
            responseObserver.onCompleted();
        });
    }
}
