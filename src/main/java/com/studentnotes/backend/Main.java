package com.studentnotes.backend;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {
  public static void main(String[] args) throws Exception {

    //  Server port (default 8080 if PORT not set)
    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", 
"8080"));

    //  Create HTTP server
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

    //  Health check endpoint
    var healthCtx = server.createContext("/health", exchange -> {
      if (!"GET".equals(exchange.getRequestMethod())) {
        exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        return;
      }
      byte[] response = "ok".getBytes();
      exchange.getResponseHeaders().add("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, response.length);
      exchange.getResponseBody().write(response);
      exchange.close();
    });
	
    server.createContext("/api/me", new MeHandler());

    healthCtx.getFilters().add(new CorsFilter()); // add CORS filter

    // Notes endpoint
    var notesCtx = server.createContext("/api/notes", new NotesHandler());
    notesCtx.getFilters().add(new CorsFilter()); // add CORS filter

    //  Profile (Me) endpoint
    var meCtx = server.createContext("/api/me", new MeHandler());
    meCtx.getFilters().add(new CorsFilter()); // add CORS filter

    //  Configure thread pool & start server
    
server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(8));
    System.out.println("Server running on port " + port);
    server.start();
  }
}


