package com.pool.service;

import com.pool.config.gateway.IplGateway;
import com.pool.record.CommonResponse;
import com.pool.record.IplData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class IplService {


    private final IplGateway iplGateway;

    public IplService(IplGateway iplGateway) {

        this.iplGateway=iplGateway;
    }

    public CommonResponse sendIplDataToChannel(IplData iplData){
        return iplGateway.sendIpldate(iplData);
    }

    public String toJson(IplData iplData){
        return iplGateway.toJson(iplData);
    }

}
