package com.pool.msg;

import com.pool.record.IplData;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class IplMessageSource implements MessageSource<IplData> {

    @Override
    public Message<IplData> receive() {
        return MessageBuilder.withPayload(new IplData(String.valueOf(new Random().nextInt(2008,2023)),Math.random() <.5?"DC":"SRH")).build();
    }
}
