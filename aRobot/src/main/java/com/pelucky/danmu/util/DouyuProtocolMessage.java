package com.pelucky.danmu.util;

import com.pelucky.danmu.DanmuApp;
import com.pelucky.danmu.RequestRobotHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DouyuProtocolMessage {
    private int[] messageLength;
    private int[] code;
    private int[] end;
    private ByteArrayOutputStream byteArrayOutputStream;
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();


    private int mRebootCount = 0;
    private int mStopSendCount = 0;
    private int mSetCDCount = 0;
    private int mTempRecordCD;//临时记录的发言CD
    private long mHasStopedTime;//禁言达到一定时间后自动放出

    private static final int STOP_THRESHOLD = 4;//触发停止数
    private static final int REBOOT_THRESHOLD = 4;//触发重启数
    private static final int CD_THRESHOLD = 4;//触发CD数
    private static final int WAKEUP_THRESHOLD = 10;//自动唤醒时间，分钟
    private static final int MIN_CD = 6000;//最小发言cd


    private static String sLeftSymbol;
    private static String sRightSymbol;
    private static String sJingChangTip;
    private static String sFLevelLowTip;
    private static String sFLevelHigh;
    private static String sFLevelTop;
    private static String s3Q;
    private static String sBanKa;
    private static String sFeiJi;
    private static String sHuoJian;
    private static String sChaoHuo;
    private static String sOther;
    private static String sGifUnkown;

    private static String sTemp;

    //唤醒词
    private static String sWakeUpWords;
    //招换到某个房间
    private static String sMagicGo;
    //招换回家
    private static String sMagicHome;
    private static String sMagic33;

    static {
        sLeftSymbol = utf2GBK("[");
        sRightSymbol = utf2GBK("]");
        sJingChangTip = utf2GBK("欢迎");
        sFLevelLowTip = utf2GBK(", 请继续提升牌子等级哟!!!");
        sFLevelHigh = utf2GBK(", 牌子好高级呀, 请好好保持哟!!!");
        sFLevelTop = utf2GBK(", 哦嚯!  粉丝牌亮瞎了我的钛合金眼!!!");
        s3Q = utf2GBK("谢谢 ");
        sOther = utf2GBK(" 送的礼物!!!");
        sBanKa = utf2GBK(" 送的办卡, 大气!!");
        sFeiJi = utf2GBK(" 送的飞机! 大气大气!!!");
        sHuoJian = utf2GBK(" 送的火箭!  嚯，您真的猛!!!!!");
        sGifUnkown = utf2GBK(" 送的什么鬼礼物? 哈, 不好意思, 我还没登记过该礼物...");

        sWakeUpWords = utf2GBK("脑阔");

        sMagicGo = utf2GBK("脑阔切换:");
        sMagicHome = utf2GBK("脑阔回家");
        sMagic33 = utf2GBK("脑阔去看姗姗");

        sTemp = utf2GBK(", 血哥今天有比赛，详情请加直播公告里的qun， 拜托大家去加加油啊!");
    }

    public DouyuProtocolMessage() {
        byteArrayOutputStream = new ByteArrayOutputStream();

        mStopSendCount = 0;
        mRebootCount = 0;
    }

    public byte[] sendMessageContent(String content) throws IOException {
        this.messageLength = new int[]{calcMessageLength(content), 0x00, 0x00, 0x00};
        this.code = new int[]{0xb1, 0x02, 0x00, 0x00};
        this.end = new int[]{0x00};

        byteArrayOutputStream.reset();
        for (int i : messageLength)
            byteArrayOutputStream.write(i);
        for (int i : messageLength)
            byteArrayOutputStream.write(i);
        for (int i : code)
            byteArrayOutputStream.write(i);
        byteArrayOutputStream.write(content.getBytes("UTF-8"));
        for (int i : end)
            byteArrayOutputStream.write(i);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Be careful about the length of content, because Chinese's char is not 1 length,
     * so you should encode it first.
     *
     * @param content
     * @return
     * @throws UnsupportedEncodingException
     */
    private int calcMessageLength(String content) throws UnsupportedEncodingException {
        return 4 + 4 + (content == null ? 0 : content.getBytes("UTF-8").length) + 1;
    }

    public void receivedMessageContent(byte[] receiveMsg, DanMu danmu) {
        String message = bytesToHex(receiveMsg);
        String utf8Message = hexStringToString(message);
        String[] array = null;
        if (utf8Message != null) {
            array = utf8Message.split("/");
        }

        // Get first "/"
        int slashIndex = message.indexOf("2F") / 2;
        String messageType = new String();
        for (int i = 18; i < slashIndex; i++) {
            messageType += (char) receiveMsg[i];
        }

        // Determine type of message
        if (messageType.equals("chatmsg")) {
            //type@=chatmsg/rid@=301712/gid@=-9999/uid@=123456/nn@=test/txt@=666/level@=1/

            if (DanmuApp.isYaBa) {
                return;
            }

            String uName = "";
            String uid = "";
            String text = "";
            if (array != null) {
                String[] tempArray;
                for (String s : array) {
                    try {
                        tempArray = s.split("=");
                        if (tempArray != null && tempArray.length == 2) {
                            if ("uid@".equals(tempArray[0])) {
                                uid = tempArray[1];
                            } else if ("txt@".equals(tempArray[0])) {
                                text = tempArray[1];
                            } else if ("nn@".equals(tempArray[0])) {
                                uName = tempArray[1];
                            }
                        }
                    } catch (Exception e) {
                    } finally {
                        tempArray = null;
                    }
                }
            }

            if (DanmuApp.sUID.equals(uid)) {
                return;
            } else if (DanmuApp.MASTER_UID.equals(uid)) {
                System.out.println("[" + uName + ": " + text + "]  " + uid);
                if (text.contains(sMagicGo)) {
                    String room = parserNumer(text);
                    if (room != null) {
                        //DanmuApp.sRoomId = room;
                        //danmu.restart(utf2GBK(null));
                        return;
                    }
                } else if (text.contains(sMagicHome)) {
                    DanmuApp.sRoomId = DanmuApp.MASTER_RID;
                    danmu.restart(null);
                    return;
                } else if (text.contains(sMagic33)) {
                    DanmuApp.sRoomId = DanmuApp.RID_33;
                    danmu.restart(null);
                    return;
                }
            }

            System.out.println("[" + uName + ": " + text + "]  " + uid);
            if (text == null) {
                return;
            }

            if ("#reboot".equals(text) || "#Reboot".equals(text) || text.contains("reboot")) {
                mRebootCount++;
                System.out.println("**** RebootCount: " + mRebootCount + " ****");
            } else if ("#stop".equals(text) || "#Stop".equals(text) || text.contains("stop")) {
                mStopSendCount++;
                System.out.println("**** StopSendCount: " + mStopSendCount + " ****");
            } else if (text.contains("cd") || text.contains("CD")) {
                System.out.println("**** SetCDCount: " + text);
                try {
                    int cd = Integer.valueOf(parserNumer(text)) * 1000;
                    if (cd != mTempRecordCD) {
                        mSetCDCount = 1;
                        mTempRecordCD = cd;
                    } else {
                        mSetCDCount++;
                    }
                    System.out.println("**** SetCDCount: " + mSetCDCount + " CD=" + cd + " ***");
                    if (mSetCDCount >= CD_THRESHOLD) {
                        mSetCDCount = 0;
                        if (cd >= MIN_CD) {
                            RequestRobotHelper.sDmDuration = cd;
                            danmu.sendTipDm(utf2GBK("@@@ 小脑阔累计收到了" + CD_THRESHOLD + "条[CD]口令, 发言CD改为 " + cd / 1000 + "S @@@"));
                        } else {
                            danmu.sendTipDm(utf2GBK("@@@ 你们是SD?? 发言CD过快, 我会被Ban掉的 @@@"));
                        }
                    }
                } catch (Exception e) {
                    System.out.print("Error " + e);
                }
                return;
            }

            //重启
            if (mRebootCount >= REBOOT_THRESHOLD) {
                mRebootCount = 0;
                mStopSendCount = 0;
                mHasStopedTime = 0;
                mSetCDCount = 0;
                danmu.restart(utf2GBK("@@@ Hi 小脑阔累计收到了" + REBOOT_THRESHOLD + "条[reboot]口令, 已经启动, 发言CD " + RequestRobotHelper.sDmDuration / 1000 + "S @@@"));
            } else {
                //正常发弹幕
                if (mStopSendCount < STOP_THRESHOLD) {
                    if (DanmuApp.isYaBa) {
                        return;
                    }
                    if (danmu.isDuringCD()) {
                        System.out.println("---> Ignore question: During-cd  <---");
                        danmu.updateLastUnHandlerQuestion(text);
                        return;
                    }
                    RequestRobotHelper.getInstance().requestAnswer(danmu, text);
                }
                //触发暂停
                else if (mStopSendCount == STOP_THRESHOLD) {
                    mStopSendCount++;
                    mHasStopedTime = System.currentTimeMillis();
                    danmu.sendTipDm(utf2GBK("@@@ 小脑阔累计收到了" + STOP_THRESHOLD + "条[stop]口令, 即将关闭, 呜呜!! @@@"));
                }
                //触发开始
                else {
                    long mid = System.currentTimeMillis() - mHasStopedTime;
                    //禁言超过N分钟自动放出来，哈哈哈
                    if (mid >= WAKEUP_THRESHOLD * 60 * 1000) {
                        danmu.sendTipDm(utf2GBK("@@@ 嘿嘿, 我爸爸让我每" + WAKEUP_THRESHOLD + "分钟自动启动, 我又活啦!!! @@@"));
                        mStopSendCount = 0;
                        return;
                    }
                    //判断是否有人问了脑阔，进行回答
                    else if (matchWord(text, sWakeUpWords)) {
                        danmu.sendTipDm(utf2GBK("@@@ 呜呜, 我被坏人封住了嘴, 快用" + REBOOT_THRESHOLD + "条[reboot]口令把我吻醒!!"));
                    } else {
                        System.out.println("Current disable DM, times=" + mid);
                    }
                    return;
                }
            }
        }
        //用户进场
        else if (messageType.equals("uenter")) {
            if (mStopSendCount >= 5) {
                //return;
            }
            if (danmu.isDuringCD()) {
                if (!DanmuApp.isYaBa) {
                    return;
                }
            }
            /** type@=uenter/rid@=109064/uid@=218448281/nn@=卖血哥的小脑阔/level@=10/ic@=avatar_v3@S201809@Sba4ff2018c66db21de0aec74e78dc8e2/nl@=7/rni@=0/el@=/sahf@=0/wgei@=0/fl@=7/   */

            //level@   :用户等级
            //fl@      :粉丝牌
            //nn@      :用户名

            String uName = null;
            int fLevel = 0;
            int uLevel = 0;

            if (array != null) {
                String[] tempArray;
                for (String s : array) {
                    try {
                        tempArray = s.split("=");
                        if (tempArray != null && tempArray.length == 2) {
                            if ("level@".equals(tempArray[0])) {
                                uLevel = Integer.valueOf(tempArray[1]);
                            } else if ("fl@".equals(tempArray[0])) {
                                fLevel = Integer.valueOf(tempArray[1]);
                            } else if ("nn@".equals(tempArray[0])) {
                                uName = tempArray[1];
                            }
                        }
                    } catch (Exception e) {
                    } finally {
                        tempArray = null;
                    }
                }
            }
            if (uName != null && (uLevel >= 40 || fLevel >= 12)) {
                String tip = sJingChangTip + " " + uName + sTemp;
               /* if (fLevel > 0) {
                    if (fLevel < 15) {
                        tip = tip + sFLevelLowTip;
                    } else if (fLevel < 26) {
                        tip = tip.concat(sFLevelHigh);
                    } else {
                        tip = tip.concat(sFLevelTop);
                    }
                }*/
                if (danmu.isDuringCD()) {
                    System.out.println("---> Ignore welcome: During-cd  <---");
                    danmu.updateLastUnHandlerQuestion(tip);
                    return;
                }
                danmu.sendDm(tip);
            }
        }
        //送礼
        else if (messageType.equals("dgb")) {
            if (mStopSendCount >= 5) {
                //return;
            }
            if (danmu.isDuringCD()) {
                if (!DanmuApp.isYaBa) {
                    return;
                }
            }

            /** type@=dgb/gfid=1/gs@=59872/gfcnt@=1/uid@=1/rid=1/gid@=-9999/nn@=someone/str@=1/level@=1/dw@=1/ */
            // gfid@=824   荧光棒
            // gfid@=750   办卡
            // gfid@=1027  药丸
            //191 100鱼丸
            //192 点赞
            //

            String uName = "";
            String gfid = "";

            if (array != null) {
                String[] tempArray;
                for (String s : array) {
                    try {
                        tempArray = s.split("=");
                        if (tempArray != null && tempArray.length == 2) {
                            if ("gfid@".equals(tempArray[0])) {
                                gfid = tempArray[1];
                            } else if ("nn@".equals(tempArray[0])) {
                                uName = tempArray[1];
                            }
                        }
                    } catch (Exception e) {
                    } finally {
                        tempArray = null;
                    }
                }
            }

            if ("750".equals(gfid)) {
                if (danmu.isDuringCD()) {
                    System.out.println("---> Ignore gif: During-cd  <---");
                    danmu.updateLastUnHandlerQuestion((s3Q + uName + sBanKa));
                    return;
                }
                danmu.sendDm(s3Q + uName + sBanKa);
            } else if ("824".equals(gfid) || "1027".equals(gfid) || "191".equals(gfid) || "192".equals(gfid)) {

            } else {
                if (danmu.isDuringCD()) {
                    System.out.println("---> Ignore gif: During-cd  <---");
                    danmu.updateLastUnHandlerQuestion((s3Q + uName + sBanKa));
                    return;
                }
                danmu.sendDm(s3Q + uName + sOther);
            }

            //System.out.println("===>Gif: " + gfid + " from: " + uName);
        }
        //鱼完暴击
        else if (messageType.equals("onlinegift")) {
            //System.out.println("onlinegift===> " + message + " \n" + message.split(message));
            //type@=onlinegift/rid@=1/uid@=1/gid@=-9999/sil@=1/if@=1/ct@=1/nn@=tester/
        }
    }

    /**
     * Change text into Chinese if need
     *
     * @param receiveMsg
     * @param indexStart
     * @param indexEnd
     * @param num
     * @return
     */
    private String changeToChinese(byte[] receiveMsg, int indexStart, int indexEnd, int num) {
        String text = new String();
        for (int i = indexStart + num; i < indexEnd; i++) {
            if (receiveMsg[i] < 32 || receiveMsg[i] > 126) {
                try {
                    text += "%" + Integer.toHexString((receiveMsg[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
                } catch (StringIndexOutOfBoundsException e) {
                    System.out.println("String sindex out of range. receiveMsg: {" + receiveMsg[i] + "}");
                    System.out.println(e.getMessage());
                }
            } else {
                text += (char) receiveMsg[i];
            }
        }
        return text;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String encodeMessage(String message) {
        message = encode(message);
        return message;
    }

    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * @param message
     * @return
     * @TODO: if '%' is in message, decode will fail
     */
    private String decodeMessage(String message) {
        String decodedMessage = message;
        try {
            decodedMessage = URLDecoder.decode(message, "utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Decode error! message: { " + message + " }");
            System.out.println(e.getMessage());
        }
        decodedMessage = decode(decodedMessage);
        return decodedMessage;
    }

    public static String encode(String str) {
        return str.replace("@", "@A").replace("/", "@S");
    }

    public String decode(String str) {
        return str.replace("@A", "@").replace("@S", "/");
    }

    public static String utf2GBK(String utf) {
        try {
            //return new String(utf.getBytes("GBK"));//转换成gbk编码
            return utf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean matchWord(String message, String word) {
        if (message == null || word == null) {
            return false;
        }
        //少于10个字，才判断是否有人询问
        if (message.length() < 10) {
            return message.contains(word);
        }
        return false;
    }

    public String parserNumer(String message) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(message);
        return m.replaceAll("").trim();
    }
}
