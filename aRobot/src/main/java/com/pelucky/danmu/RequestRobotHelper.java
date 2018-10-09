package com.pelucky.danmu;


import com.google.gson.Gson;
import com.pelucky.danmu.util.DanMu;
import com.pelucky.danmu.util.DouyuProtocolMessage;

import java.util.ArrayList;
import java.util.List;

public class RequestRobotHelper {

    public static final ArrayList<String> sIgnoreAnswers = new ArrayList();
    public static final String sExitJieLong;
    public static final String sExitJieLongProxy;

    static {
        sIgnoreAnswers.add(DouyuProtocolMessage.utf2GBK("我想要更多智慧，智慧智慧快来吧。把我变聪明。"));
        sIgnoreAnswers.add(DouyuProtocolMessage.utf2GBK("卖血哥的小脑阔在思考，妈妈说爱思考的孩子更聪明。"));
        sIgnoreAnswers.add(DouyuProtocolMessage.utf2GBK("谅我有时不聪明，你能陪着我一起成长吗？"));
        sIgnoreAnswers.add(DouyuProtocolMessage.utf2GBK("好想把脑壳打开看看。"));
        sIgnoreAnswers.add(DouyuProtocolMessage.utf2GBK("我没有听懂，靠近我一点慢慢说哦。"));
        sIgnoreAnswers.add(DouyuProtocolMessage.utf2GBK("我没听明白，再说一遍吧"));

        sExitJieLong = DouyuProtocolMessage.utf2GBK("你接错了，退出成语接龙模式！");
        sExitJieLongProxy = DouyuProtocolMessage.utf2GBK("你接错了，退出成语接龙！");
    }


    public static int sKeyIndex = 0;
    public static String[] keys = new String[]{
            "07a84a182e514fc48ef123ca49ece60a",
            "7c544fe10ce3438d9f84171560d437de",
            "64501571342244eb8c044edecf298568",
            "e9db68eeb36d43d9a9dc72ecd1c9be0e",
            "b47e0633eb63475a9a19f2fc8148de5b"
    };
    private static RequestRobotHelper sInstance;
    private DanMu danmu;

    //上一次发送弹幕的时间
    public static long sLastDMTime;
    //弹幕发送间隔
    public static int sDmDuration = 10 * 1000;

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

    public void requestAnswer(DanMu danmu, String question) {
        if (danmu == null) {
            this.danmu = null;
            return;
        }
        this.danmu = danmu;
        try {
            RobotThreadPool.getInstance().sThreadPool.execute(new RobotRunnable(question));
        } catch (Exception e) {
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
                //请求次数达到上限
                if (result.intent != null && result.intent.code == 4003) {
                    System.out.println("Warning: " + an);
                    sKeyIndex++;
                    if (sKeyIndex == keys.length - 1) {
                        sKeyIndex = 0;
                    }
                } else {
                    if (!sIgnoreAnswers.contains(an) && danmu != null) {
                        if (sExitJieLong.equals(an)) {
                            an = sExitJieLongProxy;
                            an = danmu.randomAddTails(an);
                        }
                        danmu.sendDm(an);
                    }
                }
            }
        }
    }
}
