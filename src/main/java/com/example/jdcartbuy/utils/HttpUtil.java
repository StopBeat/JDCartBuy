package com.example.jdcartbuy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class HttpUtil {

    @Autowired ProxyUtil proxyUtil;
    public HttpHeaders parseHeader(String jsonString) {
        HttpHeaders headers = new HttpHeaders();
        JSONObject json= JSON.parseObject(jsonString);
        Iterator<String> keys = json.keySet().iterator();
        while (keys.hasNext()){
            String name = keys.next();
            String value = json.getString(name);
            headers.add(name,value);
        }
        return headers;
    }

    public JSONObject postRequest(String url, String strHeaders, MultiValueMap<String,?> data,Boolean needProxy){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        if(needProxy){
            SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
            String[] proxy = proxyUtil.getProxyFromGuanLiu();
            reqfac.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1]))));
            restTemplate.setRequestFactory(reqfac);
        }
        HttpHeaders headers = new HttpHeaders();
        if(strHeaders != null){
            headers = parseHeader(strHeaders);
        }
        HttpEntity<Object> request = new HttpEntity<> (data, headers);
        return restTemplate.postForObject(url, request , JSONObject.class);
    }

    public JSONObject postRequest(String url, String strHeaders, JSONObject data,Boolean needProxy){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        if(needProxy){
            SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
            String[] proxy = proxyUtil.getProxyFromGuanLiu();
            reqfac.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1]))));
            restTemplate.setRequestFactory(reqfac);
        }
        HttpHeaders headers = new HttpHeaders();
        if(strHeaders != null){
            headers = parseHeader(strHeaders);
        }
        HttpEntity<Object> request = new HttpEntity<> (data, headers);
        return restTemplate.postForObject(url, request , JSONObject.class);
    }

    public JSONObject getRequest(String url, String strHeaders,Boolean needProxy){
        RestTemplate restTemplate = new RestTemplate();
        //中文乱码
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        if(needProxy){
            SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
            String[] proxy = proxyUtil.getProxyFromGuanLiu();
            reqfac.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1]))));
            restTemplate.setRequestFactory(reqfac);
        }
        if(strHeaders != null) {
            headers = parseHeader(strHeaders);
        }
        HttpEntity<Object> request = new HttpEntity<> (null,headers);
        ResponseEntity<JSONObject> response = restTemplate.exchange(url,HttpMethod.GET,request,JSONObject.class);
        return response.getBody();
    }


    public String getRequestFromHTML(String url, String strHeaders,Boolean needProxy){
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        if(needProxy){
            SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
            String[] proxy = proxyUtil.getProxyFromGuanLiu();
            reqfac.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1]))));
            restTemplate.setRequestFactory(reqfac);
        }
        if(strHeaders != null) {
            headers = parseHeader(strHeaders);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.GET,requestEntity, String.class);
        return response.getBody();
    }

    public String postWithParam(String url, String strHeaders, Map<String,?> data, Boolean needProxy){
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        if(needProxy){
            SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
            String[] proxy = proxyUtil.getProxyFromGuanLiu();
            reqfac.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1]))));
            restTemplate.setRequestFactory(reqfac);
        }
        if(strHeaders != null) {
            headers = parseHeader(strHeaders);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.POST,requestEntity, String.class,data);
        return response.getBody();
    }

    public String getWithParam(String url, String strHeaders, Map<String,?> data, Boolean needProxy){
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        if(needProxy){
            SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
            String[] proxy = proxyUtil.getProxyFromGuanLiu();
            reqfac.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1]))));
            restTemplate.setRequestFactory(reqfac);
        }
        if(strHeaders != null) {
            headers = parseHeader(strHeaders);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.GET,requestEntity, String.class,data);
        return response.getBody();
    }

    public ResponseEntity<String> get302Request(String url, String strHeaders,Boolean needProxy){
        RestTemplate restTemplate = new RestTemplate(new NoRedirectSimpleClientHttpRequestFactory());
        HttpHeaders headers = new HttpHeaders();
        if(needProxy){
            SimpleClientHttpRequestFactory reqfac = new SimpleClientHttpRequestFactory();
            String[] proxy = proxyUtil.getProxyFromGuanLiu();
            reqfac.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy[0], Integer.parseInt(proxy[1]))));
            restTemplate.setRequestFactory(reqfac);
        }
        if(strHeaders != null) {
            headers = parseHeader(strHeaders);
        }
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(url,HttpMethod.GET,requestEntity, String.class);
        return response;
    }

    public RestTemplate getRestTemplate(){
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringHttpMessageConverter.setWriteAcceptCharset(true);
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.ALL);
        for (int i = 0; i < restTemplate.getMessageConverters().size(); i++) {
            if (restTemplate.getMessageConverters().get(i) instanceof StringHttpMessageConverter) {
                restTemplate.getMessageConverters().remove(i);
                restTemplate.getMessageConverters().add(i, stringHttpMessageConverter);
            }
            if(restTemplate.getMessageConverters().get(i) instanceof MappingJackson2HttpMessageConverter){
                try{
                    ((MappingJackson2HttpMessageConverter) restTemplate.getMessageConverters().get(i)).setSupportedMediaTypes(mediaTypeList);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }

        return restTemplate;
    }
}
