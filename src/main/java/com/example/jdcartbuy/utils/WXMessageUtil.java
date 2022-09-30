package com.example.jdcartbuy.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WXMessageUtil {

    @Autowired HttpUtil httpUtil;
    public void sendFangTangMessage(String serviceToken, String title,String message){
        String url = "https://sctapi.ftqq.com/" + serviceToken + ".send";
        MultiValueMap<String,String> data = new LinkedMultiValueMap<String,String>();
        data.add("title",title);
        data.add("desp",message);
        httpUtil.postRequest(url,null,data,false);
    }

    public void sendWXMessage(String wxOwner,String wId,String title, String message){
        String url = "http://127.0.0.1:8073/send";
        JSONObject data = new JSONObject();
        data.put("type",100);
        data.put("msg",title+"\n"+message);
        data.put("to_wxid",wId);
        data.put("robot_wxid",wxOwner);
        httpUtil.postRequest(url,null,data,false);
    }

    public void sendZhiXiMessage(String url, String title,String content){
        MultiValueMap<String,String> data = new LinkedMultiValueMap<String,String>();
        data.add("title",title);
        data.add("content",content);
        JSONObject object = httpUtil.postRequest(url, null, data, false);
        System.out.println(object.toJSONString());
    }

}
