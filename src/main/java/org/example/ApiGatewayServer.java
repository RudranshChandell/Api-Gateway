package org.example;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ApiGatewayServer {
    public static void main(String[] args) throws IOException{
        int port =8080;
        HttpServer server =HttpServer.create(new InetSocketAddress(port),0);
        server.createContext("/",new Mock_Backend_Service);
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("✅ API Gateway started successfully on port"+port);
    }
    static class RootHandler implements HttpHandler{

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response ="Gateway says hello!";
            exchange.sendResponseHeaders(200,response.getBytes().length);

            try(OutputStream os = exchange.getResponseBody()){
                os.write(response.getBytes());
            }

        }
    }
}
