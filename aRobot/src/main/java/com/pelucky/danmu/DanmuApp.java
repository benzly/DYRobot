package com.pelucky.danmu;

import com.google.gson.Gson;
import com.pelucky.danmu.util.Danmu;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;


public class DanmuApp {

    public static void main(String[] args) {

        String danmu_server = "openbarrage.douyutv.com";
        int danmu_port = 8601;
        String auth_server = "119.90.49.86";
        int auth_port = 8076;
        String room_id = "109064";
        String username = "218448281";
        String ltkid = "79218108";
        String stk = "9503e0685f7da670";

        //175.25.20.19","port":"8094"
        //room_id = "4668973";
        //auth_server = "114.118.20.33";
        //auth_port = 8015;


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

        System.out.println("==============send DM===============");
        danmu.sendDanmu("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }

    public static String createParams(String text) {
        Params params = new Params();
        params.perception = new Perception();
        params.perception.inputText = new InputText();
        params.perception.inputText.text = text;

        params.userInfo = new UserInfo();
        params.userInfo.apiKey = "07a84a182e514fc48ef123ca49ece60a";
        params.userInfo.userId = "328760";
        return new Gson().toJson(params);
    }

    public static class Params {
        public Perception perception;
        public UserInfo userInfo;
    }

    public static class Perception {
        public InputText inputText;
    }

    public static class InputText {
        String text;
    }

    public static class UserInfo {
        String apiKey;
        String userId;
    }

    //Result
    public static class Result {
        public List<Results> results;

        public static class Results {
            public int groupType;
            public String resultType;
            public Values values;
        }

        public static class Values {
            public String text;
        }
    }

    public static class RobotRunnable implements Runnable {

        Danmu danmu;
        String input;

        public RobotRunnable(Danmu danmu, String input) {
            this.danmu = danmu;
            this.input = input;
        }

        @Override
        public void run() {
            //System.out.println("===================== " + input);
            String params = createParams(input);
            System.out.println("Request: " + params);
            String ret = RequestHelper.JsonSMS(params, "http://openapi.tuling123.com/openapi/api/v2");
            Result result = new Gson().fromJson(ret, Result.class);
            if (result != null && result.results != null && result.results.size() > 0) {
                //System.out.println("问题: " + input);
                //System.out.println("答案: " + result.results.get(0).values.text);

                /*String from = "我是弱智AI: ";
                String tip = "";
                try {
                    tip = new String(from.getBytes("GBK"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                //danmu.sendDanmu(tip + result.results.get(0).values.text);
                danmu.sendDanmu(result.results.get(0).values.text);
            }
        }
    }
}
