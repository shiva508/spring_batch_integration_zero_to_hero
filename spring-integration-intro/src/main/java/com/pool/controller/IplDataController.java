package com.pool.controller;

import com.pool.record.CommonResponse;
import com.pool.record.IplData;
import com.pool.service.IplService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ipl")
public class IplDataController {

    private final IplService iplService;

    public IplDataController(IplService iplService) {
        this.iplService = iplService;
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
}
