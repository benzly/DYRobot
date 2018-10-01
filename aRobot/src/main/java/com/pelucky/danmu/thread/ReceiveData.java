package com.pelucky.danmu.thread;

import com.pelucky.danmu.util.Danmu;
import com.pelucky.danmu.util.TcpSocketClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ReceiveData implements Runnable {
    private TcpSocketClient tcpSocketClient;
    private Danmu danmu;

    public ReceiveData(TcpSocketClient tcpSocketClient, Danmu danmu) {
        this.tcpSocketClient = tcpSocketClient;
        this.danmu = danmu;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                InputStream inputStream = tcpSocketClient.getSocket().getInputStream();
                byte[] msg = new byte[10240];
                int line = 0;
                line = inputStream.read(msg);
                if (line != -1) {
                    byteOutput.write(msg, 0, line);
                    byte[] receiveMsg = byteOutput.toByteArray();
                    tcpSocketClient.getDouyuProtocolMessage().receivedMessageContent(receiveMsg, danmu);
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
