package com.pool.controller;

import com.pool.config.gateway.IplGatewayClient;
import com.pool.record.CommonResponse;
import com.pool.record.IplData;
import com.pool.service.IplService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ipl")
public class IplDataController {

    private final IplService iplService;

    private final MessageChannel transmitterChannelRest;

    private final IplGatewayClient iplGatewayClient;

    public IplDataController(IplService iplService,
                             @Qualifier("transmitterChannelRest") MessageChannel transmitterChannelRest,
                             IplGatewayClient iplGatewayClient) {
        this.iplService = iplService;
        this.transmitterChannelRest=transmitterChannelRest;
        this.iplGatewayClient=iplGatewayClient;
    }

    @PostMapping("/add")
    public ResponseEntity<CommonResponse> sendIplData(@RequestBody IplData iplData){
        CommonResponse commonResponse = iplService.sendIplDataToChannel(iplData);
        return new ResponseEntity<>(commonResponse, HttpStatus.CREATED);
    }

    @PostMapping("/addnew")
    public ResponseEntity<String> sendIplDataNew(@RequestBody IplData iplData){
        String commonResponse = iplService.toJson(iplData);
        return new ResponseEntity<>(commonResponse, HttpStatus.CREATED);
    }

    @PostMapping("/restinput")
    public ResponseEntity<String> sendIplDataApi(@RequestBody IplData iplData){
        System.out.println("IN");
        transmitterChannelRest.send(MessageBuilder.withPayload(iplData).build());
        return new ResponseEntity<>(iplData.winner(), HttpStatus.CREATED);
    }

    @PostMapping("/restgatewayinput")
    public ResponseEntity<IplData> sendIplDataGatewayApi(@RequestBody IplData iplData){
        System.out.println("IN");
        var iplDataResult = iplGatewayClient.sendIplData(iplData);
        return new ResponseEntity<>(iplDataResult, HttpStatus.CREATED);
    }
}
