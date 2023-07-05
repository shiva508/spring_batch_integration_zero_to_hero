package com.pool.config;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class ReadFileProcessor {
    public void readMsg(Message<String> stringMessage){
        System.out.println(stringMessage.getPayload());
    }
}
