// TmdbGenreListResponse.java
package com.movie.service.tmdb.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbGenreListResponse {
  private List<TmdbGenreItem> genres;

  public List<TmdbGenreItem> getGenres() { return genres; }
  public void setGenres(List<TmdbGenreItem> genres) { this.genres = genres; }
}
