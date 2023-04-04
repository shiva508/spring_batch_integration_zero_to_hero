package com.pool.config.gateway;

import com.pool.record.IplData;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface IplGatewayClient {

    @Gateway(requestChannel = "iplGatewayTxMessageChannel",replyChannel = "iplGatewayRxMessageChannel")
    public IplData sendIplData(IplData iplData);
}
