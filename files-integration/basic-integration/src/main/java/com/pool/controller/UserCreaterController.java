package com.pool.controller;

import com.pool.config.pubsub.TradeMessageHandler;
import com.pool.record.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserCreaterController {

    @Autowired
    private MessageChannel userMessageChannel;

    @Autowired
    private QueueChannel userQueueChannel;

    @Autowired
    private PublishSubscribeChannel userPublishSubscribeChannel;

    @GetMapping("/create-user")
    public void createUser(){
        User usershiva=new User("Shiva",new Random().nextInt(400,509));
        Message<User> userMessage= MessageBuilder.withPayload(usershiva).build();
        userMessageChannel.send(userMessage,1000);
        userPublishSubscribeChannel.send(userMessage,1000);
    }

    @GetMapping("/queue-create-user")
    public void createQueueUser(){

      Message userMessage=  userQueueChannel.receive(1000);
        System.out.println(userMessage.getPayload());
    }
    @GetMapping("/pub-sub-create-user")
    public void createPubSubUser(){
        userPublishSubscribeChannel.subscribe(new TradeMessageHandler());
    }

}
