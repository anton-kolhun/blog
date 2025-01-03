package com.example.blog;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BlogHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            var uri = exchange.getRequestURI();
            if (uri.getPath().startsWith("/static/")) {
                handleResponse(exchange, uri.getPath().substring(1));
            }
            if (uri.getPath().equals("/")) {
                handleResponse(exchange, "home.html");
            }
            String htmlPage = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1) + ".html";
            handleResponse(exchange, htmlPage);
        }
        throw new RuntimeException("HTTP method not supported");
    }

    private void handleResponse(HttpExchange httpExchange, String resourcePath) throws IOException {
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            var pageURL = ClassLoader.getSystemResource(resourcePath);
            if (pageURL == null) {
                respondWith404(httpExchange);
                return;
            }
            var page = Files.readAllBytes(Paths.get(pageURL.toURI()));
            httpExchange.sendResponseHeaders(200, page.length);
            outputStream.write(page);
            outputStream.flush();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static void respondWith404(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }
}