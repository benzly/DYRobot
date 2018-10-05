package com.pelucky.danmu.thread;

import com.pelucky.danmu.util.DanMu;
import com.pelucky.danmu.util.DouyuProtocolMessage;
import com.pelucky.danmu.util.TcpSocketClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ReceiveDataAuth implements Runnable {
    private TcpSocketClient tcpSocketClient;
    private DanMu danmu;
    private boolean stop;

    public ReceiveDataAuth(TcpSocketClient tcpSocketClient, DanMu danmu) {
        this.tcpSocketClient = tcpSocketClient;
        this.danmu = danmu;
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                InputStream inputStream = tcpSocketClient.getSocket().getInputStream();
                byte[] msg = new byte[10240];
                int line = 0;
                line = inputStream.read(msg);
                if (line != -1) {
                    byteOutput.write(msg, 0, line);
                    byte[] receiveMsg = byteOutput.toByteArray();
                    //tcpSocketClient.getDouyuProtocolMessage().receivedMessageContent(receiveMsg, danmu);
                    /*if (receiveMsg != null) {
                        String ret = DouyuProtocolMessage.hexStringToString(DouyuProtocolMessage.bytesToHex(receiveMsg));
                        System.out.println("Auth: " + ret);
                    }*/
                } else {
                    //System.out.println("ReceiveData ret null");
                }
            } catch (Exception e) {
                System.out.println("Receive error:\n" + errInfo(e));
            }
        }
    }

    public static String errInfo(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
