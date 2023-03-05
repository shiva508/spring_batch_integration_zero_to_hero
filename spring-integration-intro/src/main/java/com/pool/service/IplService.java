package com.pool.service;

import com.pool.record.CommonResponse;
import com.pool.record.IplData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class IplService {

    private final DirectChannel transmitterMessageChannel;

    public IplService(@Qualifier("transmitterMessageChannel") DirectChannel transmitterMessageChannel) {
        this.transmitterMessageChannel = transmitterMessageChannel;
    }

    public CommonResponse sendIplDataToChannel(IplData iplData){
        transmitterMessageChannel.send(MessageBuilder.withPayload(iplData).setHeader("app","Shiva").build());
        return new  CommonResponse("Ok");
    }

}
