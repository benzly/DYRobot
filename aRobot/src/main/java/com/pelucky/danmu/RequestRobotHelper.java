package com.pelucky.danmu;


import com.google.gson.Gson;
import com.pelucky.danmu.util.DanMu;

import java.util.List;

public class RequestRobotHelper {

    public static int sKeyIndex = 0;
    public static String[] keys = new String[]{
            "07a84a182e514fc48ef123ca49ece60a",
            "7c544fe10ce3438d9f84171560d437de",
            "64501571342244eb8c044edecf298568",
            "e9db68eeb36d43d9a9dc72ecd1c9be0e",
            "b47e0633eb63475a9a19f2fc8148de5b"
    };
    private static RequestRobotHelper sInstance;
    DanMu danmu;
    long lastTime;
    //弹幕发送间隔
    public static int sDmDuration = 3000;

    private RequestRobotHelper() {
    }

    public static RequestRobotHelper getInstance() {
        if (sInstance == null) {
            synchronized (RequestRobotHelper.class) {
                if (sInstance == null) {
                    sInstance = new RequestRobotHelper();
                }
            }
        }
        return sInstance;
    }

    public void bindDM(DanMu danmu) {
        this.danmu = danmu;
    }

    public void requestAnswer(String question) {
        if (danmu == null) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime > sDmDuration) {
            lastTime = currentTime;
            try {
                RobotThreadPool.getInstance().sThreadPool.execute(new RobotRunnable(question));
            } catch (Exception e) {
            }
        }
    }

    public static String createParams(String text) {
        Params params = new Params();
        params.perception = new Perception();
        params.perception.inputText = new InputText();
        params.perception.inputText.text = text;

        params.userInfo = new UserInfo();
        if (sKeyIndex >= keys.length) {
            sKeyIndex = 0;
        }
        params.userInfo.apiKey = keys[sKeyIndex];
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
        public Intent intent;
        public List<Results> results;

        public static class Results {
            public int groupType;
            public String resultType;
            public Values values;
        }

        public static class Intent {
            int code;
        }

        public static class Values {
            public String text;
        }
    }

    class RobotRunnable implements Runnable {

        String input;

        public RobotRunnable(String input) {
            this.input = input;
        }

        @Override
        public void run() {
            if (danmu == null) {
                return;
            }
            String ret = RequestHelper.JsonSMS(createParams(input), "http://openapi.tuling123.com/openapi/api/v2");
            Result result = new Gson().fromJson(ret, Result.class);
            if (result != null && result.results != null && result.results.size() > 0) {
                String an = result.results.get(0).values.text;
                //System.out.println(">>>>> Ret: " + an + " ret=" + ret);
                //请求次数达到上限
                if (result.intent != null && result.intent.code == 4003) {
                    System.out.println("Warning: " + an);
                    sKeyIndex++;
                    if (sKeyIndex == keys.length - 1) {
                        sKeyIndex = 0;
                    }
                } else {
                    if (danmu != null) {
                        danmu.sendDanmu(an);
                    }
                }
            }
        }
    }
}
