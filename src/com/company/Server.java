package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by User on 003 03.02.17.
 */
public class Server {
    private final int PORT = 8887;
    ServerSocket serverSocket;
    volatile ArrayList<Socket> sockets;
    ArrayList<byte[]> nicknames;
    ArrayList<byte[]> nicknamesMode;
    boolean flag;
    int count;
    int i;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
        sockets = new ArrayList<Socket>();
        nicknames = new ArrayList<byte[]>();
        nicknamesMode = new ArrayList<byte[]>();
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
                        socketBusiness();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void socketBusiness(){
        while (true) {
            if (flag) {
                System.out.println(Thread.currentThread().getName());
                getSendInfo(i);
                flag = false;
            } else {
                getSendInfo(++i);
            }
            listnerAndSpeaker();
            break;
        }
    }

    private void getSendInfo(int k){
        System.out.println(k);
        byte[] buffer = new byte[255];
        byte[] mode1 = new byte[255];
        try {
            sockets.get(k - 1).getInputStream().read(buffer);
            sockets.get(k - 1).getInputStream().read(mode1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        char[] chars = "Пользователь ".toCharArray();
        byte[] charsBuffer = new byte[chars.length];
        byte[] mode = new byte[255];
        for (int j = 0; j < chars.length; j++) {
            if(chars[j]==' '){
                mode[j] = 0;
            }else{
                mode[j]=1;
            }
            charsBuffer[j] = (byte) chars[j];
            System.out.println(chars[j] + "  " + charsBuffer[j]);
        }
        System.out.println("---------");
        char[] chars1 = " был добавлен".toCharArray();
        byte[] charsBuffer1 = new byte[chars1.length];
        byte[] mode2 = new byte[255];
        for (int j = 0; j < chars1.length; j++) {
            if(chars1[j]==' '){
                mode2[j] = 0;
                System.out.print(mode2[j]+"  ");
            }else{
                mode2[j]=1;
                System.out.print(mode2[j]+"  ");
            }
            charsBuffer1[j] = (byte) chars1[j];
            System.out.println(chars1[j] + "  " + charsBuffer1[j]);
        }
        for (int j = 0; j < sockets.size(); j++) {
            try {
                sockets.get(j).getOutputStream().write(charsBuffer);
                sockets.get(j).getOutputStream().write(mode);
                sockets.get(j).getOutputStream().write(buffer);
                sockets.get(j).getOutputStream().write(mode1);
                sockets.get(j).getOutputStream().write(charsBuffer1);
                sockets.get(j).getOutputStream().write(mode2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        nicknames.add(buffer);
        nicknamesMode.add(mode1);
    }

    private void listnerAndSpeaker(){
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

    private void listenAndSay() {
        byte[] buffer = new byte[255];
        byte[] mode = new byte[255];
        try {
            listner(Integer.parseInt((Thread.currentThread().getName().substring(7))), buffer, mode);
            speaker(Integer.parseInt((Thread.currentThread().getName().substring(7))), buffer, mode);
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
            nicknames.remove(number - 1);
            nicknamesMode.remove(number - 1);
            System.out.println(sockets.size());
            flag = true;
            count = number;
        }
    }

    private synchronized void speaker(int number, byte[] buffer, byte[] mode) {
        for (int j = 0; j < sockets.size(); j++) {
            try {
                sockets.get(j).getOutputStream().write(buffer);
                sockets.get(j).getOutputStream().write(mode);
                sockets.get(j).getOutputStream().write(nicknames.get(number - 1));
                sockets.get(j).getOutputStream().write(nicknamesMode.get(number - 1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}