package com.studentnotes.backend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public final class FirebaseInit {
  public static void initOnce() throws Exception {
    if (FirebaseApp.getApps().isEmpty()) {
      FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build();
      FirebaseApp.initializeApp(options);
    }
  }
}

