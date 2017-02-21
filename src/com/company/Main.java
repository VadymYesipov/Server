package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class Main {

    public static void main(String[] args) {
        try {
            workServer();
        } catch (SocketException e) {
            System.out.println("her");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void workServer()throws IOException {
        Server server = new Server();
        server.work();
    }

}
