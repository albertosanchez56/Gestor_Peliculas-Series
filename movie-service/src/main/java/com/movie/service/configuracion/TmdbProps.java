// src/main/java/com/movie/service/configuracion/TmdbProps.java
package com.movie.service.configuracion;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "tmdb")
public class TmdbProps {

	private String apiKey;
	  private String baseUrl;
	  private String imagesBase;
	  private String posterSize;
	  private String backdropSize;
	  private String profileSize;   // <-- añade esto
	  private String language;
	  private String certCountry;

	// getters y setters
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getImagesBase() {
		return imagesBase;
	}

	public void setImagesBase(String imagesBase) {
		this.imagesBase = imagesBase;
	}

	public String getPosterSize() {
		return posterSize;
	}

	public void setPosterSize(String posterSize) {
		this.posterSize = posterSize;
	}

	public String getBackdropSize() {
		return backdropSize;
	}

	public void setBackdropSize(String backdropSize) {
		this.backdropSize = backdropSize;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCertCountry() {
		return certCountry;
	}

	public void setCertCountry(String certCountry) {
		this.certCountry = certCountry;
	}

	public String getProfileSize() {
		return profileSize;
	}

	public void setProfileSize(String profileSize) {
		this.profileSize = profileSize;
	}

	
}
