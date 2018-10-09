package com.pelucky.danmu;

import com.pelucky.danmu.util.DanMu;
import com.pelucky.danmu.util.DouyuProtocolMessage;

public class DanmuApp {


    public static final String MASTER_UID = "2638355";
    public static final String MASTER_RID = "109064";
    public static final String RID_33 = "4854976";

    public static final String sDMServer = "openbarrage.douyutv.com";
    public static final int sDMPort = 8601;

    public static String sRoomId = "109064";

    //小脑阔
    public static String sUID = "218445635";
    public static String sLtkid = "45874084";
    public static String sStk = "3d3f9aadd08aa91f";

    public static String sAuthServer = "114.118.20.33";
    public static int sAuthServerPort = 8013;

    public static boolean isYaBa = true;


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
