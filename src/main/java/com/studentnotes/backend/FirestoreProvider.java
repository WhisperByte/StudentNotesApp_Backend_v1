package com.studentnotes.backend;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public final class FirestoreProvider {
  private static volatile Firestore db;

  private FirestoreProvider() {}

  public static Firestore db() {
    if (db == null) {
      synchronized (FirestoreProvider.class) {
        if (db == null) {
          String projectId = System.getenv("PROJECT_ID");
          String databaseId = System.getenv("FIRESTORE_DATABASE_ID");
          if (databaseId == null || databaseId.isBlank()) {
            databaseId = "(default)";
          }
          System.out.println("Using PROJECT_ID=" + projectId + " DB=" + 
databaseId);
          db = FirestoreOptions.newBuilder()
              .setProjectId(projectId)
              .setDatabaseId(databaseId)
              .build()
              .getService();
        }
      }
    }
    return db;
  }
}

