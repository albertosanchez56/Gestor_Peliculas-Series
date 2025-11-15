// src/main/java/com/movie/service/Entidades/CastCredit.java
package com.movie.service.Entidades;

import java.time.Instant;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "cast_credit",
       uniqueConstraints = @UniqueConstraint(columnNames = {"movie_id", "tmdb_person_id"}), // evita duplicados
       indexes = {
         @Index(name = "idx_cast_movie", columnList = "movie_id"),
         @Index(name = "idx_cast_order", columnList = "order_index")
       })
@EntityListeners(AuditingEntityListener.class)
public class CastCredit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "movie_id", nullable = false)
  private Movie movie;

  @Column(name = "tmdb_person_id", nullable = false)
  private Long tmdbPersonId;

  @Column(name = "person_name", nullable = false, length = 150)
  private String personName;

  @Column(name = "character_name", length = 200)
  private String characterName;

  @Column(name = "order_index")
  private Integer orderIndex;     // TMDB 'order' (protas primero)

  @Column(name = "profile_url", length = 512)
  private String profileUrl;      // imagen de perfil (construida)

  @Column(name = "known_for_department", length = 50)
  private String knownForDepartment; // "Acting", etc.

  private Double popularity;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant updatedAt;

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public Movie getMovie() {
	return movie;
}

public void setMovie(Movie movie) {
	this.movie = movie;
}

public Long getTmdbPersonId() {
	return tmdbPersonId;
}

public void setTmdbPersonId(Long tmdbPersonId) {
	this.tmdbPersonId = tmdbPersonId;
}



public String getPersonName() {
	return personName;
}

public void setPersonName(String personName) {
	this.personName = personName;
}

public String getCharacterName() {
	return characterName;
}

public void setCharacterName(String characterName) {
	this.characterName = characterName;
}

public Integer getOrderIndex() {
	return orderIndex;
}

public void setOrderIndex(Integer orderIndex) {
	this.orderIndex = orderIndex;
}

public String getProfileUrl() {
	return profileUrl;
}

public void setProfileUrl(String profileUrl) {
	this.profileUrl = profileUrl;
}

public String getKnownForDepartment() {
	return knownForDepartment;
}

public void setKnownForDepartment(String knownForDepartment) {
	this.knownForDepartment = knownForDepartment;
}

public Double getPopularity() {
	return popularity;
}

public void setPopularity(Double popularity) {
	this.popularity = popularity;
}

public Instant getCreatedAt() {
	return createdAt;
}

public void setCreatedAt(Instant createdAt) {
	this.createdAt = createdAt;
}

public Instant getUpdatedAt() {
	return updatedAt;
}

public void setUpdatedAt(Instant updatedAt) {
	this.updatedAt = updatedAt;
}

  // getters/setters...
  
}
