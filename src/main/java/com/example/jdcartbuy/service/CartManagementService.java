package com.example.jdcartbuy.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.jdcartbuy.controller.MainController;
import com.example.jdcartbuy.utils.HttpUtil;
import com.example.jdcartbuy.utils.TimeUtil;
import com.example.jdcartbuy.utils.WXMessageUtil;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class CartManagementService {
    @Autowired
    HttpUtil httpUtil;
    @Autowired
    TimeUtil timeUtil;
    @Autowired
    WXMessageUtil wxMessageUtil;

    List<String> skuIdList;
    List<String> nameList;
    List<String> priceRevertList;
    List<String> stockStateList;
    List<String> checkedNumList;
    List<String> skuUuidList;

    Map<String,String> stockCheck;
    public TextArea logger;
    public void setLogger(TextArea l){
        logger = l;
    }
    public String getUserKey(String ck){
        String p = "user-key=(.*?);";
        Pattern r = Pattern.compile(p);
        Matcher m = r.matcher(ck);
        if (m.find()) {
            return m.group(0).substring(9, m.group(0).length() - 1);
        }
        return "";
    }
    public void getCartInfo(String ck){
        logger("获取购物车信息");
        skuIdList = new ArrayList<>();
        nameList = new ArrayList<>();
        priceRevertList = new ArrayList<>();
        stockStateList = new ArrayList<>();
        checkedNumList = new ArrayList<>();
        skuUuidList = new ArrayList<>();
        stockCheck = new LinkedHashMap<>();
        String body = "{\"serInfo\":{\"area\":\"15_1213_1214_50135\",\"user-key\":\""+getUserKey(ck)+"\"},\"cartExt\":{\"specialId\":1}}";
        String url = "https://api.m.jd.com/api?functionId={functionId}&appid={appid}&loginType={loginType}&body={body}";
        Map<String, Object> data = new HashMap<>();
        data.put("functionId","pcCart_jc_getCurrentCart");
        data.put("appid","JDC_mall_cart");
        data.put("loginType","3");
        data.put("body",body);
        String headers = "{\n" +
                "  \"cookie\": \"" + ck + "\",\n" +
                "  \"origin\": \"https://cart.jd.com\",\n" +
                "  \"referer\": \"https://cart.jd.com/\",\n" +
                "  \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\"\n" +
                "}";
        JSONObject res = JSONObject.parseObject(httpUtil.getWithParam(url, headers, data, false));
        System.out.println(res.toJSONString());
        try {
            if(res.getJSONObject("resultData").getJSONObject("cartInfo")==null){
                logger("购物车为空");
                return;
            }
        }catch (Exception e){
            logger("购物车为空");
            return;
        }
        JSONArray vendors = res.getJSONObject("resultData").getJSONObject("cartInfo").getJSONArray("vendors");
        for(int i = 0; i < vendors.size(); i++){
            JSONArray sorted = vendors.getJSONObject(i).getJSONArray("sorted");
            for(int j = 0; j < sorted.size(); j++){
                JSONObject item = sorted.getJSONObject(j).getJSONObject("item");
                if(item.getString("Name")!=null){
                    checkedNumList.add(sorted.getJSONObject(j).getString("checkedNum"));
                    nameList.add(item.getString("Name"));
                    priceRevertList.add(item.getString("priceRevert"));
                    skuIdList.add(item.getString("Id"));
                    stockStateList.add(item.getString("stockState"));
                    skuUuidList.add(item.getString("skuUuid"));
                    stockCheck.put(item.getString("Id"),item.getString("stockState"));
                }else {
                    JSONArray items = item.getJSONArray("items");
                    for(int k = 0; k < items.size(); k++){
                        JSONObject itemLow = items.getJSONObject(k).getJSONObject("item");
                        checkedNumList.add(items.getJSONObject(k).getString("checkedNum"));
                        nameList.add(itemLow.getString("Name"));
                        priceRevertList.add(itemLow.getString("priceRevert"));
                        skuIdList.add(itemLow.getString("Id"));
                        stockStateList.add(itemLow.getString("stockState"));
                        skuUuidList.add(itemLow.getString("skuUuid"));
                    }
                }
            }
        }
        for(int i = 0; i < skuIdList.size(); i++){
            logger("商品名【"+nameList.get(i)+"】，id【" + skuIdList.get(i) +"】，价格【" + priceRevertList.get(i) +"】，已选件数【" + checkedNumList.get(i) + "】，库存【" + stockStateList.get(i) + "】，skuUid【" + skuUuidList.get(i) + "】。");
        }
    }
    public void cartClearRemove(String ck){
        logger("清空购物车");
        getCartInfo(ck);
        String userKey = getUserKey(ck);
        Map<String, Object> data = new HashMap<>();
        data.put("functionId","pcCart_jc_cartClearRemove");
        data.put("appid","JDC_mall_cart");
        data.put("loginType","3");
        String body = "";
        JSONArray operations = new JSONArray();
        for(int i = 0; i < skuIdList.size(); i++){
            JSONObject object = new JSONObject();
            object.put("itemId",skuIdList.get(i));
            object.put("itemType","1");
            object.put("suitType","1");
            object.put("skuUuid","");
            object.put("storeId","");
            object.put("useUuid","");
            operations.add(object);
        }
        body = "{\"serInfo\":{\"area\":\"20_1726_1738_23019\",\"user-key\":\""+userKey+"\"},\"operations\":"+operations.toString()+"}";
        System.out.println(body);
        data.put("body",body);
        String url = "https://api.m.jd.com/api?functionId={functionId}&appid={appid}&loginType={loginType}&body={body}";
        String headers = "{\n" +
                "  \"cookie\": \"" + ck + "\",\n" +
                "  \"origin\": \"https://cart.jd.com\",\n" +
                "  \"referer\": \"https://cart.jd.com/\",\n" +
                "  \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\"\n" +
                "}";
        JSONObject res = JSONObject.parseObject(httpUtil.getWithParam(url, headers, data, false));
        System.out.println(res.toJSONString());
    }
    public void gateAction(String pid,String ck){
        sleep(2000);
        logger("添加"+pid+"到购物车");
        String url = "https://cart.jd.com/gate.action?pid="+pid+"&pcount=1&ptype=1";
        String headers = "{\n" +
                "  \"cookie\": \"" + ck + "\",\n" +
                "  \"referer\": \"https://item.jd.com/\",\n" +
                "  \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\"\n" +
                "}";
        httpUtil.get302Request(url, headers, false);
    }

    public void cartCheckSingle(String id,String ck){
        String userKey = getUserKey(ck);
        Map<String, Object> data = new HashMap<>();
        data.put("functionId","pcCart_jc_cartCheckSingle");
        data.put("appid","JDC_mall_cart");
        data.put("loginType","3");
        String body = "{\"operations\":[{\"TheSkus\":[{\"Id\":\""+id+"\",\"num\":1,\"skuUuid\":\"\",\"useUuid\":false}]}],\"serInfo\":{\"area\":\"15_1213_1214_50135\",\"user-key\":\""+userKey+"\"}}";
        data.put("body",body);
        String url = "https://api.m.jd.com/api?functionId={functionId}&appid={appid}&loginType={loginType}&body={body}";
        String headers = "{\n" +
                "  \"cookie\": \"" + ck + "\",\n" +
                "  \"origin\": \"https://cart.jd.com\",\n" +
                "  \"referer\": \"https://cart.jd.com/\",\n" +
                "  \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\"\n" +
                "}";
        JSONObject res = JSONObject.parseObject(httpUtil.getWithParam(url, headers, data, false));
    }
    public void makeAllUnchecked(String ck){
        String userKey = getUserKey(ck);
        Map<String, Object> data = new HashMap<>();
        data.put("functionId","pcCart_jc_cartUnCheckAll");
        data.put("appid","JDC_mall_cart");
        data.put("loginType","3");
        String body = "{\"serInfo\":{\"area\":\"15_1213_1214_50135\",\"user-key\":\""+userKey+"\"}}";
        data.put("body",body);
        String url = "https://api.m.jd.com/api?functionId={functionId}&appid={appid}&loginType={loginType}&body={body}";
        String headers = "{\n" +
                "  \"cookie\": \"" + ck + "\",\n" +
                "  \"origin\": \"https://cart.jd.com\",\n" +
                "  \"referer\": \"https://cart.jd.com/\",\n" +
                "  \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\"\n" +
                "}";
        JSONObject res = JSONObject.parseObject(httpUtil.getWithParam(url, headers, data, false));
    }
    public void getOrderInfo(String ck){
        String url = "https://trade.jd.com/shopping/order/getOrderInfo.action";
        String headers = "{\n" +
                "  \"accept\": \"text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9\",\n" +
                "  \"accept-encoding\": \"gzip, deflate, br\",\n" +
                "  \"accept-language\": \"zh-CN,zh;q=0.9\",\n" +
                "  \"cache-control\": \"no-cache\",\n" +
                "  \"cookie\": \"" + ck + "\",\n" +
                "  \"pragma\": \"no-cache\",\n" +
                "  \"referer\": \"https://cart.jd.com/\",\n" +
                "  \"sec-ch-ua\": \"\\\" Not A;Brand\\\";v=\\\"99\\\", \\\"Chromium\\\";v=\\\"102\\\", \\\"Google Chrome\\\";v=\\\"102\\\"\",\n" +
                "  \"sec-ch-ua-mobile\": \"?0\",\n" +
                "  \"sec-ch-ua-platform\": \"\\\"Windows\\\"\",\n" +
                "  \"sec-fetch-dest\": \"document\",\n" +
                "  \"sec-fetch-mode\": \"navigate\",\n" +
                "  \"sec-fetch-site\": \"same-site\",\n" +
                "  \"sec-fetch-user\": \"?1\",\n" +
                "  \"upgrade-insecure-requests\": \"1\",\n" +
                "  \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\"\n" +
                "}";
        String response = httpUtil.getRequestFromHTML(url, headers, false);
        submitOrder(ck);
    }
    public void submitOrder(String ck){
        String url = "https://trade.jd.com/shopping/order/submitOrder.action?&presaleStockSign=1";
        MultiValueMap<String,String> data = new LinkedMultiValueMap<>();
        data.add("overseaPurchaseCookies","");
        data.add("vendorRemarks","[]");
        data.add("submitOrderParam.sopNotPutInvoice","false");
        data.add("submitOrderParam.trackID","TestTrackId");
        data.add("submitOrderParam.presaleStockSign","1");
        data.add("submitOrderParam.ignorePriceChange","0");
        data.add("submitOrderParam.btSupport","0");
        data.add("submitOrderParam.zpjd","1");
        data.add("submitOrderParam.jxj","1");
        String headers = "{\n" +
                "  \"cookie\": \"" + ck + "\",\n" +
                "  \"origin\": \"https://trade.jd.com\",\n" +
                "  \"referer\": \"https://trade.jd.com/shopping/order/getOrderInfo.action\",\n" +
                "  \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\",\n" +
                "}";
        JSONObject object = httpUtil.postRequest(url, headers, data, false);
        if(object.getString("success").equals("true")){
            MainController.flag = false;
            try {
                wxMessageUtil.sendZhiXiMessage(MainController.pushAPI,"京东捡漏成功，请查看订单","京东捡漏成功，请查看订单");
                logger("捡漏成功，请查看");
            }catch (Exception e){
                logger("捡漏成功，请查看");
            }
        }
    }

    public void changeSkuNum(String skuId,String number,String ck){
        sleep(1000);
        logger("修改"+skuId+"为"+number+"件");
        Map<String, Object> data = new HashMap<>();
        data.put("functionId","pcCart_jc_changeSkuNum");
        data.put("appid","JDC_mall_cart");
        data.put("loginType","3");
        String body = "";
        body = "{\"operations\":[{\"TheSkus\":[{\"Id\":\""+skuId+"\",\"num\":"+number+",\"skuUuid\":\"\",\"useUuid\":false}]}],\"serInfo\":{\"area\":\"20_1726_1738_23019\"}}";
        data.put("body",body);
        String url = "https://api.m.jd.com/api?functionId={functionId}&appid={appid}&loginType={loginType}&body={body}";
        String headers = "{\n" +
                "  \"cookie\": \"" + ck + "\",\n" +
                "  \"origin\": \"https://cart.jd.com\",\n" +
                "  \"referer\": \"https://cart.jd.com/\",\n" +
                "  \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\"\n" +
                "}";
        httpUtil.getWithParam(url, headers, data, false);
    }

    public void checkCK(String ck){
        String url = "https://order.jd.com/center/list.action";
        String header = "{\n" +
                "  \"cookie\": \"" + ck + "\",\n" +
                "  \"referer\": \"https://cart.jd.com/\",\n" +
                "  \"user-agent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36\"\n" +
                "}";
        ResponseEntity<String> response = httpUtil.get302Request(url, header, false);
        if(response.getStatusCode().value()==302){
            logger("ck有误，请重新输入");
        }else {
            logger("登录成功");
        }

    }

    public void startMonitor(String ck,String targetInfo){
        List<Map<String,String>> targetList = new ArrayList<>();
        logger(targetInfo);
        String[] targetSplit = targetInfo.split("\\\n");
        logger("准备监控");
        cartClearRemove(ck);
        try {
            for(int i = 0; i < targetSplit.length; i++){
                String[] split = targetSplit[i].split(",");
                Map<String,String> target = new LinkedHashMap<>();
                for (String s : split) {
                    String[] tempTarget = s.split("\\|");
                    target.put(tempTarget[0], tempTarget[1]);
                }
                for (Map.Entry<String, String> entry : target.entrySet()) {
                    if (!skuIdList.contains(entry.getKey())) {
                        gateAction(entry.getKey(), ck);
                    }
                    changeSkuNum(entry.getKey(), entry.getValue(), ck);
                }
                targetList.add(target);
            }
        }catch (Exception e){
            logger("请确认监控目标");
            MainController.flag = false;
            return;
        }


        makeAllUnchecked(ck);
        getCartInfo(ck);
        logger("开始监控");
        while (MainController.flag){
            sleep(1000);
            getCartInfo(ck);
            for (Map<String,String> target : targetList){
                boolean goToBuy = true;
                for (Map.Entry<String, String> entry : target.entrySet()) {
                    if (!stockCheck.get(entry.getKey()).equals("有货")){
                        goToBuy = false;
                        break;
                    }
                }
                if(goToBuy){
                    for (Map.Entry<String, String> entry : target.entrySet()) {
                        logger("商品"+entry.getKey()+"有货");
                        cartCheckSingle(entry.getKey(),ck);
                    }
                    getOrderInfo(ck);
                    makeAllUnchecked(ck);
                    break;
                }
            }
        }
        logger("监控线程已关闭");
    }

    public void logger(String info){
        logger.appendText(info + "\n");
        logger.selectEnd();
        logger.deselect();
    }
    public void sleep(int time){
        try {
            //睡眠1s
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
