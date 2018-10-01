package com.pelucky.danmu.util;

import com.pelucky.danmu.thread.KeepaliveSender;
import com.pelucky.danmu.thread.ReceiveData;

import java.security.MessageDigest;
import java.util.UUID;

public class Danmu {
    TcpSocketClient tcpSocketClient;
    private TcpSocketClient tcpSocketClientAuth;
    private KeepaliveSender keepaliveSender;
    private KeepaliveSender keepaliveSenderAuth;
    private ReceiveData receiveData;
    private ReceiveData receiveDataAuth;
    private String roomID;
    private String username;
    private String ltkid;
    private String stk;

    public Danmu(String danmu_server, int danmu_port, String auth_server, int auth_port, String roomID, String username, String ltkid, String stk) {
        tcpSocketClient = new TcpSocketClient(danmu_server, danmu_port, this);
        keepaliveSender = new KeepaliveSender(tcpSocketClient);
        receiveData = new ReceiveData(tcpSocketClient, this);

        tcpSocketClientAuth = new TcpSocketClient(auth_server, auth_port, this);
        keepaliveSenderAuth = new KeepaliveSender(tcpSocketClientAuth);
        receiveDataAuth = new ReceiveData(tcpSocketClientAuth, this);

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
        tcpSocketClient.sendData("type@=loginreq/roomid@=" + roomID + "/");
        tcpSocketClient.sendData("type@=joingroup/rid@=" + roomID + "/gid@=-9999/");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        authDanmu();
    }

    private void sendKeepalive() {
        Thread thread = new Thread(keepaliveSender);
        thread.setName("ServerKeepaliveThread");
        thread.start();
    }

    private void sendAuthKeepalive() {
        Thread thread = new Thread(keepaliveSenderAuth);
        thread.setName("AuthServerReceiveThread");
        thread.start();
    }

    private void receiveData() {
        Thread thread = new Thread(receiveData);
        thread.setName("DanmuServerReceiveThread");
        thread.start();
    }

    private void receiveAuthData() {
        Thread thread = new Thread(receiveDataAuth);
        thread.setName("AuthServerReceiveThread");
        thread.start();
    }

    /**
     * Auth server, The
     */
    public void authDanmu() {
        //receiveAuthData();
        //sendAuthKeepalive();

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
