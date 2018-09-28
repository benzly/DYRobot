package com.pelucky.danmu.thread;

import com.pelucky.danmu.util.TcpSocketClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReceiveData implements Runnable {
    private TcpSocketClient tcpSocketClient;

    public ReceiveData(TcpSocketClient tcpSocketClient) {
        this.tcpSocketClient = tcpSocketClient;
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
                byteOutput.write(msg, 0, line);
                byte[] receiveMsg = byteOutput.toByteArray();
                tcpSocketClient.getDouyuProtocolMessage().receivedMessageContent(receiveMsg);
            } catch (IOException e) {
                System.out.println("Receive IO error!");
                System.out.println(e.getMessage());
            }
        }
    }
}
