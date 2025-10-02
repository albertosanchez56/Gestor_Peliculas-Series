// TmdbMovieSummary.java  (respuesta de /movie/popular, /discover, etc.)
package com.movie.service.tmdb.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieSummary {
  private long id;                    // tmdb id
  private String title;
  private String overview;            // descripción
  private String release_date;        // "yyyy-MM-dd"
  private List<Long> genre_ids;
  private String poster_path;         // "/abc.jpg"
  private String backdrop_path;       // "/xyz.jpg"
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
  public List<Long> getGenre_ids() { return genre_ids; }
  public void setGenre_ids(List<Long> genre_ids) { this.genre_ids = genre_ids; }
  public String getPoster_path() { return poster_path; }
  public void setPoster_path(String poster_path) { this.poster_path = poster_path; }
  public String getBackdrop_path() { return backdrop_path; }
  public void setBackdrop_path(String backdrop_path) { this.backdrop_path = backdrop_path; }
  public String getOriginal_language() { return original_language; }
  public void setOriginal_language(String original_language) { this.original_language = original_language; }
  public Double getVote_average() { return vote_average; }
  public void setVote_average(Double vote_average) { this.vote_average = vote_average; }
}