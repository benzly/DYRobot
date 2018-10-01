package com.pelucky.danmu.util;

import com.pelucky.danmu.thread.ReceiveData;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

public class TcpSocketClient {
    private InetAddress host;
    private int port;
    private Socket socket;
    private DouyuProtocolMessage douyuProtocolMessage;
    private Danmu danmu;

    public TcpSocketClient(String server, int port, Danmu danmu) {
        try {
            this.host = InetAddress.getByName(server);
            this.port = port;
            this.danmu = danmu;
            System.out.println("Connect to Server{" + host.getHostAddress() + "}:{" + port + "}");
            this.socket = new Socket(this.host, this.port);
            System.out.println("Open Socket successfully");
        } catch (IOException e) {
            System.out.println("Open socket fail");
            System.out.println(e.getMessage());
        }
        douyuProtocolMessage = new DouyuProtocolMessage();
    }

    public Socket getSocket() {
        return socket;
    }

    public DouyuProtocolMessage getDouyuProtocolMessage() {
        return douyuProtocolMessage;
    }

    public void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String temp = "";
    public boolean restarting = false;

    public void restart() {
        restarting = true;
        closeSocket();
        try {
            this.socket = new Socket(this.host, this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        danmu.restart();
        restarting = false;
    }

    public void sendData(String content) {
        if (restarting) {
            System.out.println("====Current is restart dm server====");
            return;
        }
        //System.out.println("SendData: " + content);
        byte[] messageContent = null;
        try {
            messageContent = douyuProtocolMessage.sendMessageContent(content);
        } catch (Exception e1) {
            System.out.println("ProtocolMessage error: \n" + ReceiveData.errInfo(e1));
        }
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(messageContent);
        } catch (Exception e) {
            System.out.println("SendData Write error: \n" + ReceiveData.errInfo(e));
            try {
                restart();
            } catch (Exception e1) {
            }
        }
    }
}
