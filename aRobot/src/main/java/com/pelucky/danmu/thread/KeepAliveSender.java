package com.pelucky.danmu.thread;

import com.pelucky.danmu.util.TcpSocketClient;

public class KeepAliveSender implements Runnable {
    private TcpSocketClient tcpSocketClient;

    public KeepAliveSender(TcpSocketClient tcpSocketClient) {
        this.tcpSocketClient = tcpSocketClient;
    }

    private boolean stop;

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            //System.out.println("===============Send KeepAlive=================");
            long unixTime = System.currentTimeMillis() / 1000L;
            this.tcpSocketClient.sendData("type@=keeplive/tick@=" + unixTime + "/");
            try {
                Thread.sleep(40000);
            } catch (Exception e) {
                System.out.println("KeepAliveSender Sleep interrupted!");
                //e.printStackTrace();
            }
        }
    }
}

