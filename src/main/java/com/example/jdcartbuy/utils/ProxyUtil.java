package com.example.jdcartbuy.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProxyUtil {
    public String[] getProxyFromGuanLiu(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://api.hailiangip.com:8422/api/getIp?type=1&num=1&pid=-1&unbindTime=600&cid=-1&orderId=O22081517185428651607&time=1661177237&sign=e8cdfd51c6c5dae746acac9fd9a6fca3&noDuplicate=0&dataType=0&lineSeparator=0&singleIp=";
        JSONObject response = restTemplate.getForObject(url,JSONObject.class);
        String ip = response.getJSONArray("data").getJSONObject(0).getString("ip");
        String port = response.getJSONArray("data").getJSONObject(0).getString("port");
        return new String[] {ip,port};
    }
}
