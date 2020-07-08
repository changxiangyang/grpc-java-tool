1.  项目结构
B2C&物流事业部产研团队 > 7.2 grpc-java-tool 工具包 > image2020-7-8_15-47-11.png

grpc-client   
rpc客户端，采用spring-boot-starter方式，可以根据配置文件自动注入对应的客户端bean，开箱即用

grpc-lib   
lib包 :  protobuf file、及自动化生成的.class文件、公共协议接口文件

grpc-server
rpc 服务端，采用自定义注解，需要依赖方完成对rpc接口协议的具体实现，并自动暴露端口供客户端调用



2. 最新依赖包
客户端
<dependency>
    <groupId>com.opay.algo</groupId>
    <artifactId>grpc-client</artifactId>
    <version>1.0.0</version>
</dependency>
服务端
<dependency>
    <groupId>com.opay.algo</groupId>
    <artifactId>grpc-server</artifactId>
    <version>1.0.0</version>
</dependency>


3. 使用方式
客户端
配置文件修改，增加调用服务端相关配置

grpc.client.enabled=true
grpc.client.serverNode[0].name="algoApiClient"
grpc.client.serverNode[0].host="127.0.0.1"
grpc.client.serverNode[0].port=19934
或

grpc:
  client:
    enabled: true
    serverNode:
      - name: "algoApiClient"
        host: "127.0.0.1"
        port: 19433
属性介绍

grpc.client.enabled 默认值: true, 当为false时不启动 grpc-clent 自动配置

grpc.client.serverNode[x] 服务端节点信息，可配置多个

grpc.client.serverNode[x].name 服务端节点名称(很关键)

grpc.client.serverNode[x].host  服务端节点地址

grpc.client.serverNode[x].port  服务端节点端口



继承com.opay.algo.client.GrpcClientConfigurer 接口绑定配置和客户端bean的对应关系

package com.opay.algo.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


/**
 * @author algo
 */
@Configuration
public class MyFacadeServerMapper implements GrpcClientConfigurer {

    /**
     * 自动注入的protoubuf协议服务实现类
     */
    @Autowired
    private HubApiClient hubApiClient;

    /**
     * 绑定服务端配置和实现类
     *   即: 手动配置客户端和服务端的关系
     * @param mapper
     */
    @Override
    public void addBinding(GrpcClientConfigurerSupport mapper) {
        /**
         * 'algoApiClient' 为配置文件中服务端配置的名称，即:grpc.client.serverNode[x].name
         */
        mapper.binding("algoApiClient", hubApiClient);
    }
}


可以开始 @Autowired 或 @Resource 等使用该bean

服务端
配置文件修改

grpc.server.enabled=true
grpc.server.port=19934
或

grpc:
  server:
  	enabled: true
  	port: 19433


实现grpc-lic中公共接口类，例如HubApiFacade

package com.opay.algo;

import com.opay.algo.annotation.GrpcService;
import com.opay.algo.facade.hub.HubApiFacade;
import oexp.common.base.Base;
import oexp.common.entity.resource.Resource;
import oexp.service.hub_api.HubApiOuterClass.GetHubByLocReq;
import oexp.service.hub_api.HubApiOuterClass.GetHubByLocRsp;
import oexp.service.hub_api.HubApiOuterClass.HubCandidate;

import java.util.ArrayList;

/**
 * 实现服务端接口
 * @author algo
 */

@GrpcService
public class MyHubApiServer implements HubApiFacade {

    @Override
    public GetHubByLocRsp getHubByLoc(GetHubByLocReq req) {
        Base.Location location = req.getLocation();
        HubCandidate build = HubCandidate.newBuilder()
                .setHub(Resource.Hub.newBuilder()
                        .setAddress("cxy")
                        .setLat(location.getLat())
                        .setLng(location.getLng())
                        .build())
                .setDist(1234)
                .build();
        ArrayList<HubCandidate> hubCandidates = new ArrayList<HubCandidate>() {{
            add(build);
        }};
        return GetHubByLocRsp.newBuilder().addAllHubList(hubCandidates).build();
    }
}
注解介绍
@GrpcService 实现类必须用此注解标记，才能被扫描工具识别为rpc服务，该注解包含了Spring原生的@Service注解


4. lib库升级
B2C&物流事业部产研团队 > 7.2 grpc-java-tool 工具包 > image2020-7-8_16-40-19.png

如果要实现新的protobuf协议文件

在proro文件中引入协议文件，编辑即可生成对应的java文件，(已配置自动生成依赖包)

syntax = "proto3";

import "base.proto";
import "resource.proto";

package oexp.service.hub_api;
option go_package = "oexp.org/lib/oexp-proto/service/hub_api";
option java_package = "oexp.service.hub_api";

// 按照位置倒序获取列表
message GetHubByLocReq {

    oexp.common.base.Location location = 1;

    // Can be 0, using default values(50000m)
    int64 limit_max_dist = 2;

    oexp.common.base.Trace trace = 99;
}

message HubCandidate {

    oexp.common.entity.resource.Hub hub = 1;

    int64 dist = 2; // unit(m)
}

message GetHubByLocRsp {
    oexp.common.base.Error error = 1;

    repeated HubCandidate hub_list = 2;
}

service HubApi {

    // 按照位置倒序获取列表
    rpc GetHubByLoc (GetHubByLocReq) returns (GetHubByLocRsp) {};
}


定义java版公共接口文件，例如截图中hub_api

package com.opay.algo.facade.hub;


import com.opay.algo.facade.base.GrpcFacade;


/**
 * GetHubByLocRsp、GetHubByLocReq自动生成的protobuf协议java文件
 */
import oexp.service.hub_api.HubApiOuterClass.GetHubByLocRsp;
import oexp.service.hub_api.HubApiOuterClass.GetHubByLocReq;

/**
 * 目前需要手动定义java公共接口，供 client 和 server 使用 
 * @author algo
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

      

如果需要提供对应grpc-client中客户端，还需要实现
B2C&物流事业部产研团队 > 7.2 grpc-java-tool 工具包 > image2020-7-8_16-52-5.png

package com.opay.algo.client;

import com.opay.algo.client.base.BaseClient;
import com.opay.algo.facade.hub.HubApiFacade;
import oexp.service.hub_api.HubApiOuterClass;
import org.springframework.stereotype.Component;


/**
 * 定义client
 * @author algo
 */
@Component
public class HubApiClient extends BaseClient implements HubApiFacade {

    @Override
    public HubApiOuterClass.GetHubByLocRsp getHubByLoc(HubApiOuterClass.GetHubByLocReq req) {
        return getStub().getHubByLoc(req);
    }
}
继承自com.opay.algo.client.base.BaseClient 实现对应的java公共接口类即可。

