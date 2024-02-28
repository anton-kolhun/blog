package com.example.blog;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class BlogApplication {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8000), 0);
        server.createContext("/", new BlogHttpHandler());
        server.start();
    }

}
