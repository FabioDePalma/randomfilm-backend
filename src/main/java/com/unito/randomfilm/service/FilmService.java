package com.unito.randomfilm.service;


import com.unito.randomfilm.entity.Film;
import com.unito.randomfilm.exception.BadRequestException;
import com.unito.randomfilm.exception.ResourceNotFoundException;
import com.unito.randomfilm.repository.FilmRepository;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmRepository repository;
    private final OmdbClient omdbClient;

    public FilmService(FilmRepository repository, OmdbClient omdbClient) {
        this.repository = repository;
        this.omdbClient = omdbClient;
    }

//    public Film getFilm(String title) {
//        return repository.findByTitleIgnoreCase(title)
//                .orElseThrow(() -> new ResourceNotFoundException("Film non trovato: " + title));
//    }

    public void insertFilm(Film film, String userEmail) {
        film.setSeen(false);
        if (repository.existsByTitle(film.getTitle())) {
            throw new BadRequestException("Film già esistente: " + film.getTitle());
        }
        film.setUserEmail(userEmail);
        repository.save(film);
    }

    public void modifyFilm(Film film, String userEmail) {
        log.info(film.getUserEmail());
        log.info(userEmail);
        log.info("sono {} dentro il service di modify modifico un film di {}",userEmail,film.getUserEmail());
        if (!repository.existsById(film.getId())) {
            throw new ResourceNotFoundException("Film non trovato: " + film.getTitle());
        }
        if (!film.getUserEmail().equals(userEmail)){
            throw new BadRequestException("Non puoi modificare un film che non hai inserito TU.");
        }
        repository.save(film);
    }
    //Transactional mi serve cosi se l'operazione va a buon fine allora la cancella, se va in errore no
    // (anche se c'è il controlo sull'id potrebbero esserci altri errori non gestiti)
    @Transactional
    public void removeFilm(Film film, String userEmail) {
        if (!repository.existsById(film.getId())){
            throw new ResourceNotFoundException("Film non trovato: " + film.getTitle());
        }

        if (!film.getUserEmail().equals(userEmail)){

            log.info("user uguale user eliminazione");
            throw new BadRequestException("Non puoi eliminare un film che non hai inserito TU.");
        }
        repository.delete(film);
    }

    public List<Film> getAllFilms() {
        log.info("sono dentro getallfilms service");
        return repository.findAll();
    }

    public Page<Film> getFilms(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Film> searchFilmsByTitle(String title, Pageable pageable) {
        return repository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public Film getFilmFromExternal(String title, Integer year) {
        return omdbClient.fetchFilm(title, year);
    }

    public Film getRandomFilm() {
        List<Long> filmsId = repository.findUnseenFilmIds();
        if (filmsId.isEmpty()) {
            throw new ResourceNotFoundException("Nessun film presente nel database");
        }

        // Genera un indice casuale
        int randomIndex = (int) (Math.random() * filmsId.size());
        Long randomId = filmsId.get(randomIndex);
        return repository.findById(randomId)
                .orElseThrow(() -> new ResourceNotFoundException("Film con ID " + randomId + " non trovato"));
    }

    @Transactional
    public void modifyFilmSeen(Film film) {
        if (!repository.existsById(film.getId())) {
            throw new ResourceNotFoundException("Film non trovato: " + film.getTitle());
        }
        log.info("Film con aggiornato con nuovo stato seen a {}", film.getSeen());
        repository.updateSeenStatus(film.getSeen(), film.getId());

    }
}
