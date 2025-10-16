package com.studentnotes.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class Json {
  public static final Gson gson = new 
GsonBuilder().serializeNulls().create();
}

