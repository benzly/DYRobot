package com.pelucky.danmu;

import com.pelucky.danmu.util.Danmu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class DanmuApp {

    public static void main(String[] args) {

        /*if (true) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String ret = sendGet("https://www.douyu.com/109064", null);
                    System.out.println("=====================");
                    System.out.println(ret);
                }
            }).start();
            return;
        }*/


        String danmu_server = "openbarrage.douyutv.com";
        int danmu_port = 8601;
        String auth_server = "175.25.20.18";
        int auth_port = 8089;
        String room_id = "109064";
        String username = "weibo_2J0pAvg1";
        String ltkid = "40872147";
        String stk = "521eaaa917780317";

        //豆花
        room_id = "4668973";
        auth_server = "175.25.20.19";
        auth_port = 8093;

        Danmu danmu = new Danmu(danmu_server, danmu_port, auth_server, auth_port, room_id, username, ltkid, stk);

        System.out.println("==============start init===============");
        danmu.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("==============start Auth===============");
        danmu.authDanmu();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 50; i++) {
            System.out.println("==============send DM===============");
            danmu.sendDanmu("#OOOOOOO-" + /*new Date().toString() +*/ "-OOOOOOO");
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString;
            if (param != null) {
                urlNameString = url + "?" + param;
            } else {
                urlNameString = url;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
