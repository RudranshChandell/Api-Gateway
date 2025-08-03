package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Mock_Backend_Service {
    public static void main(String[] args)throws IOException {
        int port=9001;
        HttpServer server=HttpServer.create(new InetSocketAddress(port),0);
        server.createContext("/",new BackendHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("✅ Mock Backend Server started on port");
    }

    private static class BackendHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response="Hello from the Mock Backend!";
            exchange.sendResponseHeaders(200,response.getBytes().length);
            try(OutputStream os =exchange.getResponseBody()){
                os.write(response.getBytes());
            }
        }
    }
}
