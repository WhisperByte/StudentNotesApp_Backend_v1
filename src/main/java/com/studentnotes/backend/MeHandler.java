package com.studentnotes.backend;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.sun.net.httpserver.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MeHandler implements HttpHandler {
  @Override public void handle(HttpExchange ex) {
    try {
      if ("OPTIONS".equals(ex.getRequestMethod())) { CorsHelper.apply(ex); 
ex.sendResponseHeaders(204, -1); ex.close(); return; }
      String uid = 
Auth.requireUid(ex.getRequestHeaders().getFirst("Authorization"));

      if ("GET".equals(ex.getRequestMethod())) {
        UserRecord ur = FirebaseAuth.getInstance().getUser(uid);
        var body = Json.gson.toJson(Map.of(
          "uid", ur.getUid(),
          "email", ur.getEmail(),
          "displayName", ur.getDisplayName(),
          "photoUrl", ur.getPhotoUrl()
        ));
        CorsHelper.apply(ex);
        ex.getResponseHeaders().set("Content-Type","application/json");
        ex.sendResponseHeaders(200, 
body.getBytes(StandardCharsets.UTF_8).length);
        ex.getResponseBody().write(body.getBytes(StandardCharsets.UTF_8)); 
ex.close();
      } else if ("PATCH".equals(ex.getRequestMethod())) {
        String raw = new String(ex.getRequestBody().readAllBytes(), 
StandardCharsets.UTF_8);
        var json = Json.gson.fromJson(raw, 
com.google.gson.JsonObject.class);

        UserRecord.UpdateRequest req = new UserRecord.UpdateRequest(uid);
        if (json.has("displayName") && 
!json.get("displayName").isJsonNull()) 
req.setDisplayName(json.get("displayName").getAsString());
        if (json.has("photoUrl") && !json.get("photoUrl").isJsonNull()) 
req.setPhotoUrl(json.get("photoUrl").getAsString());
        UserRecord ur = FirebaseAuth.getInstance().updateUser(req);

        var body = Json.gson.toJson(Map.of(
          "uid", ur.getUid(), "email", ur.getEmail(),
          "displayName", ur.getDisplayName(), "photoUrl", ur.getPhotoUrl()
        ));
        CorsHelper.apply(ex);
        ex.getResponseHeaders().set("Content-Type","application/json");
        ex.sendResponseHeaders(200, 
body.getBytes(StandardCharsets.UTF_8).length);
        ex.getResponseBody().write(body.getBytes(StandardCharsets.UTF_8)); 
ex.close();
      } else {
        CorsHelper.apply(ex); ex.sendResponseHeaders(405, -1); ex.close();
      }
    } catch (Auth.Unauthorized u) {
      try { CorsHelper.apply(ex); var b = 
Json.gson.toJson(Map.of("error","unauthorized")).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type","application/json"); 
ex.sendResponseHeaders(401, b.length); ex.getResponseBody().write(b); 
ex.close(); } catch (Exception ignored) {}
    } catch (Exception e) {
      e.printStackTrace();
      try { CorsHelper.apply(ex); var b = Json.gson.toJson(Map.of("error", 
e.toString())).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type","application/json"); 
ex.sendResponseHeaders(500, b.length); ex.getResponseBody().write(b); 
ex.close(); } catch (Exception ignored) {}
    }
  }
}

