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
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().set("Allow", "GET");
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith("/static/") || path.startsWith("/pagefind/")) {
            handleResponse(exchange, path.substring(1));
        } else if (path.equals("/")) {
            handleResponse(exchange, "home.html");
        } else {
            handleResponse(exchange, path.substring(path.lastIndexOf("/") + 1) + ".html");
        }
    }

    private static String contentType(String path) {
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".wasm")) return "application/wasm";
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        return "application/octet-stream";
    }

    private void handleResponse(HttpExchange httpExchange, String resourcePath) throws IOException {
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            var pageURL = ClassLoader.getSystemResource(resourcePath);
            if (pageURL == null) {
                respondWith404(httpExchange);
                return;
            }
            var page = Files.readAllBytes(Paths.get(pageURL.toURI()));
            httpExchange.getResponseHeaders().set("Content-Type", contentType(resourcePath));
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