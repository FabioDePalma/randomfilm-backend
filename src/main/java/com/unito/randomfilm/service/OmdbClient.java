package com.unito.randomfilm.service;

import com.unito.randomfilm.dto.OmdbFilmResponse;
import com.unito.randomfilm.entity.Film;
import com.unito.randomfilm.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;



@RegisterReflectionForBinding(OmdbFilmResponse.class)
@Service
public class OmdbClient {
    private static final Logger log = LoggerFactory.getLogger(OmdbClient.class);
    private final RestTemplate restTemplate;

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    public OmdbClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Film fetchFilm(String title, Integer year) {
        String url = "http://www.omdbapi.com/?t=" + title + "&apikey=" + omdbApiKey;
        if (year != null)
            url = url + "&y=" + year;
        try {
            // Faccio la chiamata HTTP tramite RestTemplate
            OmdbFilmResponse response = restTemplate.getForObject(url, OmdbFilmResponse.class);


            if (response == null || "False".equalsIgnoreCase(response.getResponse())) {
                throw new ResourceNotFoundException("Film non trovato nell'API esterna: " + title);
            }
            Film film = new Film();
            film.setTitle(response.getTitle());
            //Questo mi serve perchè l'anno deve essere un numero
            try {
                if (response.getYear() != null && !response.getYear().trim().isEmpty()) {
                    String yearStr = response.getYear().replaceAll("[^0-9].*", "");
                    film.setYear(Integer.valueOf(yearStr));
                }
            } catch (NumberFormatException e) {
                log.warn("Impossibile convertire l'anno '{}' in numero per il film '{}'",
                        response.getYear(), response.getTitle());
                film.setYear(null);
            }


            film.setDirector(response.getDirector());
            film.setDuration(response.getRuntime());
            film.setGenre(response.getGenre());
            film.setSeen(false);
            film.setPoster(response.getPoster());

            return film;
        } catch (RestClientException e) {
            log.error("Errore nella chiamata all'API OMDB per il film: {}", title, e);
            throw new ResourceNotFoundException("Errore nella chiamata all'API esterna per il film: " + title);
        }
    }
}
