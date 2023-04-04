package com.pool.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IplService {
    public void iplScheduleInfo(){
        log.info("Ipl api is called to fetch latest info");
    }

    public void iplUserName(String userName){
        log.info("required user name======:"+userName);
    }
}
