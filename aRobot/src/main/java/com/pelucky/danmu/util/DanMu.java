package com.pelucky.danmu.util;

import com.pelucky.danmu.DanmuApp;
import com.pelucky.danmu.RequestRobotHelper;
import com.pelucky.danmu.thread.KeepAliveSender;
import com.pelucky.danmu.thread.KeepAliveSenderAuth;
import com.pelucky.danmu.thread.ReceiveData;
import com.pelucky.danmu.thread.ReceiveDataAuth;

import java.util.HashMap;
import java.util.LinkedList;
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

    private TimerTask timerTask = new TimerTask();
    private HashMap<String, Long> sRecentlyDms = new HashMap<>();
    private LinkedList<String> mTipDMRequests = new LinkedList<String>();
    private String mLastUnHandlerAnswer;
    private String mLastUnHandlerQuestion;
    public static final String sChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    public interface OnDMCallback {
        void onReboot(String tip);
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

        timerTask.start();
    }

    public void start(String startTip) {
        System.out.println("==============start init [" + roomID + "]===============");
        //发送登陆请求
        tcpSocketClient.sendData("type@=loginreq/roomid@=" + roomID + "/");
        //发送入组请求
        sleep(1000);
        tcpSocketClient.sendData("type@=joingroup/rid@=" + roomID + "/gid@=-9999/");
        //开始心跳发送
        sleep(1000);
        startSendKeepalive();
        //开始弹幕组验证
        sleep(2000);
        System.out.println("==============start Auth===============");
        authDanmu();
        //发送弹幕提示1
        sleep(2000);
        System.out.println("==============send Tips DM1===============");
        //sendTipDm(startTip);
        //发送弹幕提示2
        System.out.println("==============send Tips DM2===============");
        //sendTipDm(randomAddTails(DouyuProtocolMessage.utf2GBK("^^^^[ 小脑阔弹幕控制口令上线啦, 大家可以点我主页查看! ]^^^^")));
        //开始接收弹幕
        startReceiveData();
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateLastUnHandlerQuestion(String question) {
        mLastUnHandlerQuestion = question;
    }

    public void restart(String tip) {
        if (callback != null) {
            try {
                //tcpSocketClient.sendData("type@=logout/");
                //tcpSocketClientAuth.sendData("type@=logout/");

                mTipDMRequests.clear();
                mLastUnHandlerAnswer = null;
                mLastUnHandlerQuestion = null;

                keepaliveSender.stop();
                keepaliveSenderAuth.stop();
                receiveData.stop();
                receiveDataAuth.stop();
                timerTask.stop = true;

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

            callback.onReboot(tip);
        }
    }

    private void startSendKeepalive() {
        thread1 = new Thread(keepaliveSender);
        thread1.setName("ServerKeepaliveThread");
        thread1.start();
    }

    private void startSendAuthKeepalive() {
        thread2 = new Thread(keepaliveSenderAuth);
        thread2.setName("AuthServerReceiveThread");
        thread2.start();
    }

    private void startReceiveData() {
        thread3 = new Thread(receiveData);
        thread3.setName("DanmuServerReceiveThread");
        thread3.start();
    }

    private void startReceiveAuthData() {
        thread4 = new Thread(receiveDataAuth);
        thread4.setName("AuthServerReceiveThread");
        thread4.start();
    }

    /**
     * Auth server, The
     */
    private void authDanmu() {
        //startReceiveAuthData();
        startSendAuthKeepalive();

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String vk = MD5Util.MD5(timestamp + "7oE9nPEG9xXV69phU31FYCLUagKeYtsF" + uuid);
        //String vk = MD5Util.MD5(timestamp + "r5*^5;}2#\\${XF[h+;'./.Q'1;,-]f'p[" + uuid);// vk参数

        System.out.println(">>>>>> auth: name=" + username + " roomId=" + roomID + " ltkid=" + ltkid + " stk=" + stk);

        String loginreqInfo = "type@=loginreq/username@=" + username + "/ct@=0/password@=/roomid@=" + roomID
                + "/devid@=" + uuid + "/rt@=" + timestamp + "/vk@=" + vk + "/ver@=20150929/aver@=2017073111/ltkid@="
                + ltkid + "/biz@=1/stk@=" + stk + "/";

        tcpSocketClientAuth.sendData(loginreqInfo);
    }

    public boolean isDuringCD() {
        return System.currentTimeMillis() - RequestRobotHelper.sLastDMTime < RequestRobotHelper.sDmDuration;
    }

    public boolean checksRecentDm(String dm) {
        if (dm == null) {
            return false;
        }
        Long lastTime = sRecentlyDms.get(dm);
        long current = System.currentTimeMillis();
        if (lastTime == null) {
            sRecentlyDms.put(dm, current);
            return true;
        } else {
            //20分钟内重复弹幕不发送
            if (current - lastTime >= 30 * 60 * 1000) {
                sRecentlyDms.put(dm, current);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean hasBeingSendTipDm() {
        return mTipDMRequests.size() > 0;
    }

    //随机添加小尾巴，防止消息相同
    public String randomAddTails(String message) {
        return message + sChars.charAt((int) (Math.random() * sChars.length()));
    }

    private class TimerTask extends Thread {

        public long sleep = RequestRobotHelper.sDmDuration;
        public boolean stop;

        @Override
        public void run() {
            while (!stop) {
                long interval = System.currentTimeMillis() - RequestRobotHelper.sLastDMTime;
                if (interval >= RequestRobotHelper.sDmDuration) {
                    if (mTipDMRequests.size() > 0) {
                        sendTipDm(mTipDMRequests.removeLast());
                        mTipDMRequests.clear();
                    } else if (mLastUnHandlerAnswer != null) {
                        sendDm(mLastUnHandlerAnswer);
                        mLastUnHandlerAnswer = null;
                    } else if (mLastUnHandlerQuestion != null) {
                        if (!DanmuApp.isYaBa) {
                            RequestRobotHelper.getInstance().requestAnswer(DanMu.this, mLastUnHandlerQuestion);
                        }
                        mLastUnHandlerQuestion = null;
                    }
                    sleep = RequestRobotHelper.sDmDuration;
                } else {
                    sleep = RequestRobotHelper.sDmDuration - interval;
                }

                try {
                    Thread.sleep(sleep);
                } catch (Exception e) {
                }
            }
        }
    }

    public void sendTipDm(String message) {
        if (isDuringCD()) {
            //放入队列中，挨个发送
            System.out.println("---> Delay Tip DM: During-cd <---");
            mTipDMRequests.add(message);
            return;
        }
        sendDm(message);
    }

    public void sendDm(String message) {
        if (isDuringCD()) {
            System.out.println("---> Ignore DM: During-cd  <---");
            mLastUnHandlerAnswer = message;
            return;
        }
        if (hasBeingSendTipDm()) {
            System.out.println("---> Ignore DM: Has being tips <---");
            return;
        }
        if (!checksRecentDm(message)) {
            message = randomAddTails(message);
        }

        sendData(message);
    }

    private void sendData(String message) {
        message = DouyuProtocolMessage.encodeMessage(message);
        System.out.println(")))))))))--> [" + message + "]");
        tcpSocketClientAuth.sendData("type@=chatmessage/receiver@=0/content@=" + message + "/scope@=/col@=0/pid@=/p2p@=0/nc@=0/rev@=0/ifs@=0/");
        RequestRobotHelper.sLastDMTime = System.currentTimeMillis();
    }
}
