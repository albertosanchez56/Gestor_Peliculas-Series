// TmdbMovieDetail.java  (respuesta de /movie/{id})
package com.movie.service.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieDetail {
  private long id;
  private String title;
  private String overview;
  private String release_date;
  private Integer runtime;            // minutos
  private String poster_path;
  private String backdrop_path;
  private String original_language;
  private Double vote_average;

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getOverview() { return overview; }
  public void setOverview(String overview) { this.overview = overview; }
  public String getRelease_date() { return release_date; }
  public void setRelease_date(String release_date) { this.release_date = release_date; }
  public Integer getRuntime() { return runtime; }
  public void setRuntime(Integer runtime) { this.runtime = runtime; }
  public String getPoster_path() { return poster_path; }
  public void setPoster_path(String poster_path) { this.poster_path = poster_path; }
  public String getBackdrop_path() { return backdrop_path; }
  public void setBackdrop_path(String backdrop_path) { this.backdrop_path = backdrop_path; }
  public String getOriginal_language() { return original_language; }
  public void setOriginal_language(String original_language) { this.original_language = original_language; }
  public Double getVote_average() { return vote_average; }
  public void setVote_average(Double vote_average) { this.vote_average = vote_average; }
}
