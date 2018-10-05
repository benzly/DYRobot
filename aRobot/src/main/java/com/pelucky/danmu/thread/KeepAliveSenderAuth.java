package com.pelucky.danmu.thread;

import com.pelucky.danmu.util.TcpSocketClient;

public class KeepAliveSenderAuth implements Runnable {
    private TcpSocketClient tcpSocketClient;

    public KeepAliveSenderAuth(TcpSocketClient tcpSocketClient) {
        this.tcpSocketClient = tcpSocketClient;
    }

    private boolean stop;

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            //System.out.println("===============Send KeepAliveAuth=================");
            long unixTime = System.currentTimeMillis() / 1000L;
            this.tcpSocketClient.sendData("type@=keeplive/tick@=" + unixTime + "/");
            try {
                Thread.sleep(20000);
            } catch (Exception e) {
                System.out.println("KeepAliveSenderAuth Sleep interrupted!");
                //e.printStackTrace();
            }
        }
    }
}
