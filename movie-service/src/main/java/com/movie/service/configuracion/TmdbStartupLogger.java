package com.movie.service.configuracion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TmdbStartupLogger implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TmdbStartupLogger.class);

    private final TmdbProps tmdbProps;

    public TmdbStartupLogger(TmdbProps tmdbProps) {
        this.tmdbProps = tmdbProps;
    }

    @Override
    public void run(ApplicationArguments args) {
        String key = tmdbProps.getApiKey();
        boolean empty = key == null || key.isBlank();
        if (empty) {
            log.warn("TMDB: tmdb.api-key está vacía. Configura TMDB_API_KEY para importar desde The Movie DB.");
        } else {
            log.info("TMDB: api-key configurada ({} caracteres). Bearer auth: {}", key.length(), tmdbProps.isUseBearerAuth());
        }
    }
}
