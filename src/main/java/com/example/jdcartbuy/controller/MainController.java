package com.example.jdcartbuy.controller;

import com.example.jdcartbuy.JdCartBuyApplication;
import com.example.jdcartbuy.service.CartManagementService;
import com.example.jdcartbuy.utils.WXMessageUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class MainController implements Initializable {
    @FXML
    private Stage primaryStage;
    public Button getCartInfo;
    public Button cartClearRemove;
    public Button login;
    public Button targetConfirm;
    public Button startMonitor;
    public Button endMonitor;
    public Button cleanLogger;
    public Button push;

    public TextField ckInput;
    public TextField pushInput;
    public Label targetLabel;

    public TextArea target;
    public TextArea logger;
    public static String ck;
    public static String targetInfo;

    public static boolean flag;

    public static String  pushAPI;

    @Autowired
    CartManagementService cartManagementService;
    @Autowired
    WXMessageUtil wxMessageUtil;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        ck = ck.replace("\"","\\\"");
//        String finalCk = ck;
        flag = false;
        ck = "";
        targetInfo = "";
        logger.setWrapText(true);
        primaryStage = JdCartBuyApplication.getStage();
        cartManagementService.setLogger(logger);
        login.setOnAction(event -> {
            ck = ckInput.getText();
            ck = ck.replace("\"","\\\"");
            cartManagementService.checkCK(ck);
        });

        targetConfirm.setOnAction(event -> {
            targetInfo = target.getText();
            logger("确认监控信息：");
            logger(targetInfo);
        });
        push.setOnAction(event -> {
            pushAPI = pushInput.getText();
            try {
                wxMessageUtil.sendZhiXiMessage(pushAPI,"测试信息","测试信息");
                logger("成功绑定微信推送API：" + pushAPI);
            }catch (Exception e){
                logger("推送API有误，请检查——" + pushAPI);
            }

        });
        cartClearRemove.setOnAction(event -> {
            cartManagementService.cartClearRemove(ck);
        });
        getCartInfo.setOnAction(event -> {
            cartManagementService.getCartInfo(ck);
        });
        endMonitor.setOnAction(event -> {
            flag = false;
        });
        cleanLogger.setOnAction(event -> {
            logger.clear();
        });
        startMonitor.setOnAction(event -> {
            if(!flag){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flag = true;
                        cartManagementService.startMonitor(ck,targetInfo);
                    }
                }).start();
            }else {
                logger("监控线程已开启，请先停止");
            }
        });
    }
    public void logger(String info){
        logger.appendText(info + "\n");
        logger.selectEnd();
        logger.deselect();
    }

}