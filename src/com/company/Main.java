package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.work();
    }
}
