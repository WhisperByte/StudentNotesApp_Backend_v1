package com.studentnotes.backend;

import com.sun.net.httpserver.HttpExchange;

public final class CorsHelper {
  // TODO: replace with your real frontend origin(s)
  private static final String ORIGIN = 
"https://your-frontend.example.com";
  public static void apply(HttpExchange ex) {
    var h = ex.getResponseHeaders();
    String reqOrigin = ex.getRequestHeaders().getFirst("Origin");
    // allow specific origin in prod; for dev you can echo back or use 
http://localhost:3000
    String allowed = System.getenv().getOrDefault("FRONTEND_ORIGIN", 
"http://localhost:3000");
if (reqOrigin != null && reqOrigin.equals(allowed)) {
  h.set("Access-Control-Allow-Origin", reqOrigin);
} else {
  h.set("Access-Control-Allow-Origin", allowed);
}

    h.set("Vary", "Origin");
    h.set("Access-Control-Allow-Credentials", "true");
    h.set("Access-Control-Allow-Headers", "Authorization, Content-Type");
    h.set("Access-Control-Allow-Methods", 
"GET,POST,PATCH,DELETE,OPTIONS");
  }
}

