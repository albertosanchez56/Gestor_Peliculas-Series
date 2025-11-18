package com.movie.service.DTO;

public record CastCreditDTO(
	    Long id,
	    Long tmdbPersonId,
	    String personName,
	    String characterName,
	    Integer orderIndex,
	    String knownForDepartment,
	    Double popularity,
	    String profileUrl
	) {
	    public static CastCreditDTO from(com.movie.service.Entidades.CastCredit c) {
	        return new CastCreditDTO(
	            c.getId(),
	            c.getTmdbPersonId(),
	            c.getPersonName(),
	            c.getCharacterName(),
	            c.getOrderIndex(),
	            c.getKnownForDepartment(),
	            c.getPopularity(),
	            c.getProfileUrl()
	        );
	    }
	}