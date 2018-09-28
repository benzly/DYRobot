package com.pelucky.danmu.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TcpSocketClient {
    private InetAddress host;
    private int port;
    private Socket socket;
    private DouyuProtocolMessage douyuProtocolMessage;

    public TcpSocketClient(String server, int port) {
        try {
            this.host = InetAddress.getByName(server);
            this.port = port;
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

    public void sendData(String content) {
        byte[] messageContent = null;
        try {
            messageContent = douyuProtocolMessage.sendMessageContent(content);
        } catch (IOException e1) {
            System.out.println(e1.getMessage());
        }
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(messageContent);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
