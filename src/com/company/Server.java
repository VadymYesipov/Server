package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by User on 003 03.02.17.
 */
public class Server {
    private final int port = 8888;
    ServerSocket serverSocket;
    ArrayList<Socket> sockets;
    DataInputStream[] dataInputStreams;
    DataOutputStream[] dataOutputStream;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Server() throws IOException {
        serverSocket = new ServerSocket(port);
        sockets = new ArrayList<Socket>();
        while (true) {
            sockets.add(serverSocket.accept());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        int k = 0;
                        for (Socket socket : sockets) {
                            try {
                                while (socket.getInputStream().available() > 0) {
                                    k = socket.getInputStream().read();
                                    for (Socket socket2 : sockets) {
                                        socket2.getOutputStream().write(k);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }
}
