package com.pelucky.danmu;

import com.pelucky.danmu.util.Danmu;

import java.io.InputStream;
import java.util.Properties;


public class DanmuApp {

    public static void main(String[] args) {
        Properties properties = new Properties();
        InputStream inputStream = null;

        String danmu_server = null;
        int danmu_port = 0;
        String auth_server = null;
        int auth_port = 0;
        String room_id = null;
        String username = null;
        String ltkid = null;
        String stk = null;
       /* try {
            inputStream = DanmuApp.class.getClassLoader().getResourceAsStream("../../config.properties");
            properties.load(inputStream);
            danmu_server = properties.getProperty("danmu_server", "openbarrage.douyutv.com");
            danmu_port = Integer.valueOf(properties.getProperty("danmu_port", "8601"));
            auth_server = properties.getProperty("auth_server", "119.90.49.89");
            auth_port = Integer.valueOf(properties.getProperty("auth_port", "8092"));
            room_id = properties.getProperty("room_id");
            username = properties.getProperty("username");
            ltkid = properties.getProperty("ltkid");
            stk = properties.getProperty("stk");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.info(e.getMessage());
                }
            }
        }*/

        danmu_server = "openbarrage.douyutv.com";
        danmu_port = 8601;
        auth_server = "116.242.0.39";
        auth_port = 8092;
        room_id = "109064";
        username = "weibo_2J0pAvg1";
        ltkid = "40872147";
        stk = "521eaaa917780317";

        Danmu danmu = new Danmu(danmu_server, danmu_port, auth_server, auth_port, room_id, username, ltkid, stk);

        danmu.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        danmu.authDanmu();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //danmu.sendDanmu("#签到 " + new Date().toString());
    }
}
