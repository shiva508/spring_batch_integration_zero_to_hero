package com.pool.service;

import com.pool.record.CommonResponse;
import com.pool.record.IplData;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@Component
public class IplIntService {

    @ServiceActivator(inputChannel = "transmitterMessageChannel")
    public void iplDataMessage(Message<IplData> iplDataMessage){
        MessageChannel replyChannel = (MessageChannel)iplDataMessage.getHeaders().getReplyChannel();
        MessageBuilder.fromMessage(iplDataMessage);
        var payload = iplDataMessage.getPayload();
        var stringMessage = MessageBuilder.withPayload(new CommonResponse(payload.winner() +"OK")).build();
        assert replyChannel != null;
        replyChannel.send(stringMessage);
    }

    @ServiceActivator(inputChannel = "transmitterMessageChannel.toJson")
    public void toJson(Message<?> iplDataMessage){
        MessageChannel replyChannel = (MessageChannel)iplDataMessage.getHeaders().getReplyChannel();
        System.out.println(iplDataMessage);
        System.out.println(iplDataMessage.getPayload());
        assert replyChannel != null;
        replyChannel.send(MessageBuilder.withPayload(iplDataMessage.getPayload()).build());
    }
}
