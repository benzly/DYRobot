package com.pelucky.danmu.util;

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
    private String mLastUnHandlerWelcomeOrGif;
    public static final String[] sCharArray = new String[]{
            "\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83D\uDE03", "\uD83D\uDE04", "\uD83D\uDE05", "\uD83D\uDE06"
            , "\uD83D\uDE09", "\uD83D\uDE0A", "\uD83D\uDE0B", "\uD83D\uDE0E", "\uD83D\uDE0D", "\uD83D\uDE18", "\uD83D\uDE17"
            , "\uD83D\uDE19", "\uD83D\uDE1A", "\uD83D\uDE07", "\uD83D\uDE10", "\uD83D\uDE11", "\uD83D\uDE36", "\uD83D\uDE0F"
            , "\uD83D\uDE23", "\uD83D\uDE25", "\uD83D\uDE2E", "\uD83D\uDE2F", "\uD83D\uDE2A", "\uD83D\uDE2B", "\uD83D\uDE34"
            , "\uD83D\uDE0C", "\uD83D\uDE1B", "\uD83D\uDE1C", "\uD83D\uDE1D", "\uD83D\uDE12", "\uD83D\uDE13", "\uD83D\uDE14"
            , "\uD83D\uDE15", "\uD83D\uDE32", "\uD83D\uDC70", "\uD83D\uDC7C", "\uD83D\uDC86", "\uD83D\uDC87", "\uD83D\uDE4D"
            , "\uD83D\uDE4E", "\uD83D\uDE45", "\uD83D\uDE46", "\uD83D\uDC81", "\uD83D\uDE4B", "\uD83D\uDE47", "\uD83D\uDE4C"
            , "\uD83D\uDE4F", "\uD83D\uDC64", "\uD83D\uDC65", "\uD83D\uDEB6", "\uD83C\uDFC3", "\uD83D\uDC6F", "\uD83D\uDC83"
            , "\uD83D\uDC6B", "\uD83D\uDC6C", "\uD83D\uDC6D", "\uD83D\uDC8F", "\uD83D\uDC6A", "\uD83D\uDCAA", "\uD83D\uDC4B"
            , "\uD83D\uDC4F", "\uD83D\uDC50", "\uD83D\uDC63", "\uD83D\uDC40", "\uD83D\uDC42", "\uD83D\uDC43", "\uD83D\uDC45"
            , "\uD83D\uDC44", "\uD83D\uDC8B", "\uD83D\uDC53", "\uD83D\uDC54", "\uD83D\uDC55", "\uD83D\uDC56", "\uD83D\uDC57"
            , "\uD83D\uDC58", "\uD83D\uDC59", "\uD83D\uDC5A", "\uD83D\uDC5B", "\uD83D\uDC5C", "\uD83D\uDC5D", "\uD83C\uDF92"
            , "\uD83D\uDCBC", "\uD83D\uDC5E", "\uD83D\uDC5F", "\uD83D\uDC60", "\uD83D\uDC61", "\uD83D\uDC62", "\uD83D\uDC51"
            , "\uD83D\uDC52", "\uD83C\uDFA9", "\uD83C\uDF93", "\uD83D\uDC84", "\uD83D\uDC85", "\uD83D\uDC8D", "\uD83C\uDF02"
            , "\uD83D\uDE48", "\uD83D\uDE49", "\uD83D\uDE4A", "\uD83D\uDC35", "\uD83D\uDE3C", "\uD83D\uDC0B", "\uD83D\uDC2C"
            , "\uD83D\uDC1F", "\uD83D\uDC20", "\uD83D\uDC21", "\uD83D\uDC19", "\uD83D\uDC1A", "\uD83D\uDC0C", "\uD83D\uDC1B"
            , "\uD83D\uDC1C", "\uD83D\uDC1C", "\uD83D\uDC1D", "\uD83D\uDC1E", "\uD83E\uDD8B", "\uD83D\uDC90", "\uD83C\uDF38"
            , "\uD83D\uDCAE", "\uD83C\uDF39", "\uD83C\uDF3A", "\uD83C\uDF3B", "\uD83C\uDF3C", "\uD83C\uDF37", "\uD83C\uDF31"
            , "\uD83C\uDF32", "\uD83C\uDF33", "\uD83C\uDF34", "\uD83C\uDF35", "\uD83C\uDF3E", "\uD83C\uDF3F", "\uD83C\uDF40"
            , "\uD83C\uDF41", "\uD83C\uDF42", "\uD83C\uDF43", "\uD83D\uDC95", "\uD83D\uDC96", "\uD83D\uDC97", "\uD83D\uDC99"
            , "\uD83C\uDF0D", "\uD83C\uDF0E", "\uD83C\uDF3E", "\uD83C\uDF3F", "\uD83C\uDF40", "\uD83C\uDF41", "\uD83C\uDF42"
    };
    public static final String sSmile = "\uD83D\uDE04\uD83D\uDE04\uD83D\uDE04";
    public static final String sCry = "\uD83D\uDE2D\uD83D\uDE2D\uD83D\uDE2D";
    public static final String sSb = "\uD83D\uDE31\uD83D\uDE31\uD83D\uDE31";

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
        sendTipDm(startTip);
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

    public void updateLastUnHandlerWelcomeOrGif(String welcomeOrGif) {
        mLastUnHandlerWelcomeOrGif = welcomeOrGif;
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
        return System.currentTimeMillis() - RequestRobotHelper.sLastDMTime < RequestRobotHelper.getDmDuration();
    }

    public boolean checksRecentDm(String dm) {
        if (dm == null) {
            return false;
        }

        if (dm != null) {
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
    public static String randomAddTails(String message) {
        return message + sCharArray[(int) (Math.random() * sCharArray.length)];
    }

    private class TimerTask extends Thread {

        public long sleep = RequestRobotHelper.getDmDuration();
        public boolean stop;

        @Override
        public void run() {
            while (!stop) {
                long interval = System.currentTimeMillis() - RequestRobotHelper.sLastDMTime;
                if (interval >= RequestRobotHelper.getDmDuration()) {
                    if (mTipDMRequests.size() > 0) {
                        sendTipDm(mTipDMRequests.removeLast());
                        mTipDMRequests.clear();
                    } else if (mLastUnHandlerAnswer != null) {
                        sendDm(mLastUnHandlerAnswer);
                        mLastUnHandlerAnswer = null;
                    } else if (mLastUnHandlerQuestion != null) {
                        RequestRobotHelper.getInstance().requestAnswer(DanMu.this, mLastUnHandlerQuestion);
                        mLastUnHandlerQuestion = null;
                    } else if (mLastUnHandlerWelcomeOrGif != null) {
                        sendDm(mLastUnHandlerWelcomeOrGif);
                        mLastUnHandlerWelcomeOrGif = null;
                    }
                    sleep = RequestRobotHelper.getDmDuration();
                } else {
                    sleep = RequestRobotHelper.getDmDuration() - interval;
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
            //mLastUnHandlerAnswer = message;
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
        //📢
        //🔔
        //🔫
        //👉
        //🔈🔉🔊
        message = DouyuProtocolMessage.encodeMessage("\uD83D\uDCE2 " + message);
        System.out.println(")))))))))--> [" + message + "]  interval=" + (System.currentTimeMillis() - RequestRobotHelper.sLastDMTime));
        tcpSocketClientAuth.sendData("type@=chatmessage/receiver@=0/content@=" + message + "/scope@=/col@=0/pid@=/p2p@=0/nc@=0/rev@=0/ifs@=0/");
        RequestRobotHelper.sLastDMTime = System.currentTimeMillis();
    }


    //http://www.fhdq.net/emoji.html
}
