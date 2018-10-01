package com.pelucky.danmu;

import com.pelucky.danmu.util.DanMu;

public class DanmuApp {


    public static void main(String[] args) {
        new RobotStartManager().start();
    }


    static class RobotStartManager implements DanMu.OnDMCallback {

        String danmu_server = "openbarrage.douyutv.com";
        int danmu_port = 8601;

        String auth_server = "114.118.20.36";
        int auth_port = 8014;
        String room_id = "109064";
        String username = "218448281";
        String ltkid = "79218108";
        String stk = "9503e0685f7da670";
        DanMu danmu;

        public void start() {
            System.out.println("==============start init===============");
            room_id = "109064";
            auth_server = "114.118.20.33";
            auth_port = 8013;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    danmu = new DanMu(danmu_server, danmu_port, auth_server, auth_port, room_id, username, ltkid, stk, RobotStartManager.this);
                    danmu.start();

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("==============start Auth===============");
                    danmu.authDanmu();

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("==============send DM===============");
                    String utf = "你的小脑阔已上线, CD " + RequestRobotHelper.sDmDuration / 1000 + "S";
                    String tip = "";
                    try {
                        tip = new String(utf.getBytes("GBK"));
                    } catch (Exception e) {
                    }
                    danmu.sendDanmu("@@@ " + tip + " @@@");
                }
            }).start();
        }

        @Override
        public void onReboot() {
            System.out.println("==============Restart init===============");
            start();
        }
    }
}
