package com.pelucky.danmu;

import com.pelucky.danmu.util.Danmu;

public class DanmuApp {


    public static void main(String[] args) {
        String danmu_server = "openbarrage.douyutv.com";
        int danmu_port = 8601;


        String auth_server = "114.118.20.36";
        int auth_port = 8014;
        String room_id = "109064";
        String username = "218448281";
        String ltkid = "79218108";
        String stk = "9503e0685f7da670";

        //175.25.20.19","port":"8094"
        //room_id = "4668973";
        //auth_server = "114.118.20.33";
        //auth_port = 8015;

        System.out.println("==============start init===============");
        Danmu danmu = new Danmu(danmu_server, danmu_port, auth_server, auth_port, room_id, username, ltkid, stk);
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
        danmu.sendDanmu("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
}
