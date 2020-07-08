package com.opay.algo.client;

import com.opay.algo.client.base.BaseClient;
import com.opay.algo.facade.hub.HubApiFacade;
import oexp.service.hub_api.HubApiOuterClass;
import org.springframework.stereotype.Component;


/**
 * @author cxy
 */
@Component("hubApiFacade")
public class HubApiClient extends BaseClient implements HubApiFacade {

    @Override
    public HubApiOuterClass.GetHubByLocRsp getHubByLoc(HubApiOuterClass.GetHubByLocReq req) {
        return getStub().getHubByLoc(req);
    }
}
