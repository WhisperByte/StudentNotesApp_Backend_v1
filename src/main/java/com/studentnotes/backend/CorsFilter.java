package com.studentnotes.backend;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class CorsFilter extends Filter {
  @Override public String description() { return "CORS filter"; }

  @Override public void doFilter(HttpExchange ex, Chain chain) throws 
IOException {
    CorsHelper.apply(ex);
    if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
      ex.sendResponseHeaders(204, -1);
      ex.close();
      return;
    }
    chain.doFilter(ex);
  }
}

