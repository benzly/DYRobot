package com.pelucky.danmu.thread;

import com.pelucky.danmu.util.TcpSocketClient;

public class KeepaliveSender implements Runnable {
    private TcpSocketClient tcpSocketClient;

    public KeepaliveSender(TcpSocketClient tcpSocketClient) {
        this.tcpSocketClient = tcpSocketClient;
    }

    @Override
    public void run() {
        while (true) {
            long unixTime = System.currentTimeMillis() / 1000L;
            this.tcpSocketClient.sendData("type@=keeplive/tick@=" + unixTime + "/");
            try {
                Thread.sleep(40000);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted!");
                e.printStackTrace();
            }
        }
    }
}