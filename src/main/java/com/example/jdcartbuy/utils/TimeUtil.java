package com.example.jdcartbuy.utils;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TimeUtil {
    public String getTimestamp(){
        Date date = new Date();
        return String.valueOf(date.getTime());
    }
}
