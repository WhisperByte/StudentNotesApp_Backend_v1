package com.studentnotes.backend;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NotesHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange ex) throws IOException {
    final String method = ex.getRequestMethod();
    try {
      if ("POST".equalsIgnoreCase(method)) {
        handleCreate(ex);
        return;
      } else if ("GET".equalsIgnoreCase(method)) {
        handleList(ex);
        return;
      } else {
        ex.sendResponseHeaders(405, -1); // Method Not Allowed
        ex.close();
        return;
      }
    } catch (Auth.Unauthorized u) {
      byte[] out = Json.gson.toJson(Map.of("error", "unauthorized")).getBytes(StandardCharsets.UTF_8);
      ex.getResponseHeaders().set("Content-Type", "application/json");
      ex.sendResponseHeaders(401, out.length);
      ex.getResponseBody().write(out);
      ex.close();
    } catch (Exception e) {
      e.printStackTrace();
      byte[] out = Json.gson.toJson(Map.of("error", e.toString())).getBytes(StandardCharsets.UTF_8);
      ex.getResponseHeaders().set("Content-Type", "application/json");
      ex.sendResponseHeaders(500, out.length);
      ex.getResponseBody().write(out);
      ex.close();
    }
  }

  // POST /api/notes — create a note
  private void handleCreate(HttpExchange ex) throws Exception {
    // Verify Firebase ID token and get uid
    String uid = Auth.requireUid(ex.getRequestHeaders().getFirst("Authorization"));

    // Parse request JSON
    String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    JsonObject json = Json.gson.fromJson(body, JsonObject.class);

    String title = (json != null && json.has("title") && !json.get("title").isJsonNull())
        ? json.get("title").getAsString()
        : null;
    String text = (json != null && json.has("body") && !json.get("body").isJsonNull())
        ? json.get("body").getAsString()
        : "";

    if (title == null || title.isBlank()) {
      byte[] out = Json.gson.toJson(Map.of("error", "title is required")).getBytes(StandardCharsets.UTF_8);
      ex.getResponseHeaders().set("Content-Type", "application/json");
      ex.sendResponseHeaders(400, out.length);
      ex.getResponseBody().write(out);
      ex.close();
      return;
    }

    Firestore db = FirestoreProvider.db();

    Map<String, Object> note = new HashMap<>();
    note.put("ownerId", uid); // tie to the authenticated user
    note.put("title", title);
    note.put("body", text);
    note.put("createdAt", System.currentTimeMillis());
    note.put("updatedAt", System.currentTimeMillis());

    DocumentReference ref = db.collection("notes").add(note).get();

    byte[] out = Json.gson.toJson(Map.of("id", ref.getId())).getBytes(StandardCharsets.UTF_8);
    ex.getResponseHeaders().set("Content-Type", "application/json");
    ex.sendResponseHeaders(201, out.length);
    ex.getResponseBody().write(out);
    ex.close();
  }

  // GET /api/notes — list notes for the authenticated user (newest first)
  private void handleList(HttpExchange ex) throws Exception {
    String uid = Auth.requireUid(ex.getRequestHeaders().getFirst("Authorization"));
    Firestore db = FirestoreProvider.db();

    Query q = db.collection("notes")
        .whereEqualTo("ownerId", uid)
        .orderBy("createdAt", Query.Direction.DESCENDING);

    var snapshot = q.get().get();

    List<Map<String, Object>> items = new ArrayList<>();
    for (QueryDocumentSnapshot d : snapshot.getDocuments()) {
      Map<String, Object> m = new HashMap<>(d.getData());
      m.put("id", d.getId());
      items.add(m);
    }

    byte[] out = Json.gson.toJson(items).getBytes(StandardCharsets.UTF_8);
    ex.getResponseHeaders().set("Content-Type", "application/json");
    ex.sendResponseHeaders(200, out.length);
    ex.getResponseBody().write(out);
    ex.close();
  }
}

