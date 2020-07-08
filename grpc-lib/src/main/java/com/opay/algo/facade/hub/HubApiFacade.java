package com.opay.algo.facade.hub;


import com.opay.algo.facade.base.GrpcFacade;
import oexp.service.hub_api.HubApiOuterClass.GetHubByLocRsp;
import oexp.service.hub_api.HubApiOuterClass.GetHubByLocReq;

/**
 * @author cxy
 */
public interface HubApiFacade extends GrpcFacade {

    /**
     * 获取hub通过loc
     *
     * @param req
     * @return
     */
    GetHubByLocRsp getHubByLoc(GetHubByLocReq req);
}
