package com.opay.algo.properties;

//import com.sun.istack.internal.NotNull;
import lombok.Data;

/**
 * @author cxy
 */
@Data
public class GrpcStubNodeProperties {

    /**
     * 名称
     */
    //@NotNull
    private String name;

    /**
     * host
     */
    private String host;

    /**
     * 端口
     */
    private int port;

//    /**
//     * 服务列表
//     */
//    private List<String> serverList;
}
