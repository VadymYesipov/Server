package com.company;

import javafx.application.Application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by User on 003 03.02.17.
 */
public class Server {
    private final int PORT = 8887;
    ServerSocket serverSocket;
    volatile ArrayList<Socket> sockets;
    boolean flag;
    int count;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
        sockets = new ArrayList<Socket>();
    }

    protected void work() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println(Thread.currentThread().getName());
                    acceptSocket();
                }
            }
        }).start();
    }

    private void acceptSocket() {
        try {
            if (sockets.add(serverSocket.accept())) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (flag) {
                            System.out.println(Thread.currentThread().getName());
                            flag = false;
                            //System.exit(0);
                        }
                        while (true) {
                            if (flag) {
                                break;
                            } else if (count > 0) {
                                Thread.currentThread().setName("Thread-" + count);
                                System.out.println(Thread.currentThread().getName());
                                listenAndSay();
                            } else if (count == 0) {
                                System.out.println(Thread.currentThread().getName());
                                listenAndSay();
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenAndSay() {
        byte[] buffer = new byte[255];
        byte[] mode = new byte[255];
        try {
            listner(Integer.parseInt((Thread.currentThread().getName().substring(7))), buffer, mode);
            speaker(buffer, mode);
        } catch (Exception e) {
            return;
        }
    }

    private void listner(int number, byte[] buffer, byte[] mode) {
        try {
            sockets.get(number - 1).getInputStream().read(buffer);
            sockets.get(number - 1).getInputStream().read(mode);
        } catch (IOException e) {
            sockets.remove(number - 1);
            System.out.println(sockets.size());
            flag = true;
            count = number;
        }
    }

    private synchronized void speaker(byte[] buffer, byte[] mode) {
        for (int j = 0; j < sockets.size(); j++) {
            try {
                sockets.get(j).getOutputStream().write(buffer);
                sockets.get(j).getOutputStream().write(mode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}