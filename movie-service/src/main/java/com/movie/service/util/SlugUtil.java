// src/main/java/com/movie/service/util/SlugUtil.java
package com.movie.service.util;

import java.text.Normalizer;

public final class SlugUtil {
  private SlugUtil(){}

  public static String slugify(String input) {
    if (input == null) return "";
    String n = Normalizer.normalize(input, Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    n = n.toLowerCase().trim();
    n = n.replaceAll("[^a-z0-9]+", "-");
    n = n.replaceAll("(^-|-$)", "");
    return n.isBlank() ? "n-a" : n;
  }
}
