package com.pelucky.danmu;

import com.google.gson.Gson;
import com.pelucky.danmu.util.Danmu;

import java.util.Date;
import java.util.List;


public class DanmuApp {

    public static void main(String[] args) {

        String danmu_server = "openbarrage.douyutv.com";
        int danmu_port = 8601;
        String auth_server = "175.25.20.18";
        int auth_port = 8089;
        String room_id = "109064";
        String username = "weibo_2J0pAvg1";
        String ltkid = "40872149";
        String stk = "79cb8a0fb3f86376";

        //豆花
        //room_id = "4668973";
        //auth_server = "175.25.20.19";
        //auth_port = 8093;

        room_id = "109064";
        auth_server = "114.118.20.31";
        auth_port = 8015;

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
        danmu.sendDanmu("# 开始测试自动聊天机器人 " + ", " + new Date().toString());
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

    public static class RobotThread extends Thread implements Runnable {

        Danmu danmu;
        String input;

        public RobotThread(Danmu danmu, String input) {
            this.danmu = danmu;
            this.input = input;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("===================== " + input);
            String params = createParams(input);
            System.out.println("Request: " + params);
            String ret = RequestHelper.JsonSMS(params, "http://openapi.tuling123.com/openapi/api/v2");
            Result result = new Gson().fromJson(ret, Result.class);
            if (result != null && result.results != null && result.results.size() > 0) {
                System.out.println("问题: " + input);
                System.out.println("答案: " + result.results.get(0).values.text);

                danmu.sendDanmu("Ai: " + result.results.get(0).values.text);
            }
        }
    }
}
