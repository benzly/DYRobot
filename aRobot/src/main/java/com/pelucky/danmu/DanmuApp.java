package com.pelucky.danmu;

import com.pelucky.danmu.util.DanMu;
import com.pelucky.danmu.util.DouyuProtocolMessage;

public class DanmuApp {

    //我想要更多智慧，智慧智慧快来吧。把我变聪明。
    //卖血哥的小脑阔在思考，妈妈说爱思考的孩子更聪明。
    //原谅我有时不聪明，你能陪着我一起成长吗？
    //你接错了，退出成语接龙模式！
    //好想把脑壳打开看看。
    //我没有听懂，靠近我一点慢慢说哦。
    //我没听明白，再说一遍吧

    public static final String MASTER_UID = "2638355";
    public static final String MASTER_RID = "109064";
    public static final String RID_33 = "4854976";

    public static final String sDMServer = "openbarrage.douyutv.com";
    public static final int sDMPort = 8601;

    public static String sRoomId = "109064";

    //小脑阔
    public static String sUID = "218445635";
    public static String sLtkid = "45874081";
    public static String sStk = "512c5e70dce1d996";

    public static String sAuthServer = "114.118.20.37";
    public static int sAuthServerPort = 8013;

    public static boolean isYaBa = false;


    public static final String sInitTip;
    public static final String sInitTipYB;

    static {
        sInitTip = DouyuProtocolMessage.utf2GBK("@@@ Hi 你的小脑阔已上线, 发言CD " + RequestRobotHelper.sDmDuration / 1000 + "S @@@");
        sInitTipYB = "@@@ 小脑阔已悲伤上线, 不再回答!!! @@@";

        //卖血哥的小脑阔
        //sUID = "218445635";
        //sLtkid = "45874079";
        //sStk = "c558da63f0bc4bb3";

        //sRoomId = "4205173";
    }

    public static void main(String[] args) {
        String tip;
        if (isYaBa) {
            tip = sInitTipYB;
        } else {
            tip = sInitTip;
        }
        new RobotStartManager().start(tip);
    }

    static class RobotStartManager implements DanMu.OnDMCallback {
        DanMu danmu;

        public void start(String args) {
            final String tip = args + " " + DanMu.sChars.charAt((int) (Math.random() * DanMu.sChars.length()));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    danmu = new DanMu(sDMServer, sDMPort, sAuthServer, sAuthServerPort, sRoomId, sUID, sLtkid, sStk, RobotStartManager.this);
                    danmu.start(tip);
                }
            }).start();
        }

        @Override
        public void onReboot(String tip) {
            System.out.println("==============Restart init===============");
            if (tip == null) {
                if (isYaBa) {
                    tip = sInitTipYB;
                } else {
                    tip = sInitTip;
                }
            }
            try {
                start(tip);
            } catch (Exception e) {
            }
        }
    }
}
