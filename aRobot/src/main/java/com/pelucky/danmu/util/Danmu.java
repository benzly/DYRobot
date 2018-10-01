package com.pelucky.danmu.util;

import com.pelucky.danmu.thread.KeepAliveSender;
import com.pelucky.danmu.thread.KeepAliveSenderAuth;
import com.pelucky.danmu.thread.ReceiveData;
import com.pelucky.danmu.thread.ReceiveDataAuth;

import java.security.MessageDigest;
import java.util.UUID;

public class DanMu {
    private TcpSocketClient tcpSocketClient;
    private TcpSocketClient tcpSocketClientAuth;
    private KeepAliveSender keepaliveSender;
    private KeepAliveSenderAuth keepaliveSenderAuth;
    private ReceiveData receiveData;
    private ReceiveDataAuth receiveDataAuth;
    private String roomID;
    private String username;
    private String ltkid;
    private String stk;
    private OnDMCallback callback;

    private Thread thread1;
    private Thread thread2;
    private Thread thread3;
    private Thread thread4;


    public interface OnDMCallback {
        void onReboot();
    }

    public DanMu(String danmu_server, int danmu_port, String auth_server, int auth_port, String roomID, String username, String ltkid, String stk, OnDMCallback callback) {
        tcpSocketClient = new TcpSocketClient(danmu_server, danmu_port, this);
        keepaliveSender = new KeepAliveSender(tcpSocketClient);
        receiveData = new ReceiveData(tcpSocketClient, this);
        this.callback = callback;

        tcpSocketClientAuth = new TcpSocketClient(auth_server, auth_port, this);
        keepaliveSenderAuth = new KeepAliveSenderAuth(tcpSocketClientAuth);
        receiveDataAuth = new ReceiveDataAuth(tcpSocketClientAuth, this);

        this.roomID = roomID;
        this.username = username;
        this.ltkid = ltkid;
        this.stk = stk;
    }

    public void start() {
        System.out.println(">>>>[ " + roomID + " ]<<<<");
        receiveData();
        sendKeepalive();
        tcpSocketClient.sendData("type@=loginreq/roomid@=" + roomID + "/");
        tcpSocketClient.sendData("type@=joingroup/rid@=" + roomID + "/gid@=-9999/");
    }

    public void restart() {
        if (callback != null) {
            try {
                tcpSocketClient.sendData("type@=logout/");
                tcpSocketClientAuth.sendData("type@=logout/");

                keepaliveSender.stop();
                keepaliveSenderAuth.stop();
                receiveData.stop();
                receiveDataAuth.stop();

                if (thread1 != null) {
                    thread1.interrupt();
                }
                if (thread2 != null) {
                    thread2.interrupt();
                }
                if (thread3 != null) {
                    thread3.interrupt();
                }
                if (thread4 != null) {
                    thread4.interrupt();
                }
            } catch (Exception e) {

            }

            callback.onReboot();
        }
    }

    private void sendKeepalive() {
        thread1 = new Thread(keepaliveSender);
        thread1.setName("ServerKeepaliveThread");
        thread1.start();
    }

    private void sendAuthKeepalive() {
        thread2 = new Thread(keepaliveSenderAuth);
        thread2.setName("AuthServerReceiveThread");
        thread2.start();
    }

    private void receiveData() {
        thread3 = new Thread(receiveData);
        thread3.setName("DanmuServerReceiveThread");
        thread3.start();
    }

    private void receiveAuthData() {
        //thread4 = new Thread(receiveDataAuth);
        //thread4.setName("AuthServerReceiveThread");
        //thread4.start();
    }

    /**
     * Auth server, The
     */
    public void authDanmu() {
       // receiveAuthData();
        sendAuthKeepalive();

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String vk = MD5Util.MD5(timestamp + "7oE9nPEG9xXV69phU31FYCLUagKeYtsF" + uuid);
        //String vk = MD5Util.MD5(timestamp + "r5*^5;}2#\\${XF[h+;'./.Q'1;,-]f'p[" + uuid);// vk参数

        System.out.println(">>>>>> auth: name=" + username + " roomId=" + roomID + " ltkid=" + ltkid + " stk=" + stk);

        String loginreqInfo = "type@=loginreq/username@=" + username + "/ct@=0/password@=/roomid@=" + roomID
                + "/devid@=" + uuid + "/rt@=" + timestamp + "/vk@=" + vk + "/ver@=20150929/aver@=2017073111/ltkid@="
                + ltkid + "/biz@=1/stk@=" + stk + "/";

        tcpSocketClientAuth.sendData(loginreqInfo);

        TcpSocketClient.temp = loginreqInfo;
    }

    public void sendDanmu(String message) {
        message = DouyuProtocolMessage.encodeMessage(message);
        System.out.println("-----> Send message: {" + message + ")");
        tcpSocketClientAuth.sendData("type@=chatmessage/receiver@=0/content@=" + message + "/scope@=/col@=1/pid@=/p2p@=0/nc@=0/rev@=0/ifs@=0/");
    }
}

/**
 * https://github.com/brucezz/DouyuCrawler
 */
class MD5Util {
    public static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            byte[] md = mdInst.digest(s.getBytes());
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (byte b : md) {
                str[k++] = hexDigits[b >>> 4 & 0xf];
                str[k++] = hexDigits[b & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
