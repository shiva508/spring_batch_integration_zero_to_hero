package com.pool.config.gateway;

import com.pool.record.CommonResponse;
import com.pool.record.IplData;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface IplGateway {
    @Gateway(requestChannel = "transmitterMessageChannel")
    CommonResponse sendIpldate(IplData iplData);

    @Gateway(requestChannel = "transmitterMessageChannel.toJson")
    String toJson(IplData iplData);

    @Gateway(requestChannel = "transmitterMessageChannel")
    IplData toObject(String iplData);

}
