package com.studentnotes.backend;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public final class Auth {
  public static String requireUid(String authorizationHeader) throws 
Unauthorized {
    if (authorizationHeader == null || 
!authorizationHeader.startsWith("Bearer "))
      throw new Unauthorized("Missing Bearer token");
    String idToken = authorizationHeader.substring(7);
    try {
      FirebaseToken decoded = 
FirebaseAuth.getInstance().verifyIdToken(idToken);
      return decoded.getUid();
    } catch (Exception e) {
      throw new Unauthorized("Invalid token");
    }
  }
  public static class Unauthorized extends Exception { public 
Unauthorized(String m){ super(m); } }
}

