package com.example.jdcartbuy;

import com.example.jdcartbuy.view.MainView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JdCartBuyApplication extends AbstractJavaFxApplicationSupport {
    public static void main(String[] args) {
        launch(JdCartBuyApplication.class, MainView.class, args);
    }
}
