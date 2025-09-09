package com.unito.randomfilm.Controller;

import com.unito.randomfilm.entity.Film;
import com.unito.randomfilm.dto.UserInfo;
import com.unito.randomfilm.service.FilmService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
//@CrossOrigin(
//        origins = {"${app.frontend.url}"},
//        allowCredentials = "true",
//        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS}
//)
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService service;

    public FilmController(FilmService service) {
        this.service = service;
    }

//    @GetMapping("/film/{title}")
//    public ResponseEntity<Film> getFilm(@PathVariable String title) {
//        log.info("Accessing film: {}", title);
//        return ResponseEntity.ok(service.getFilm(title));
//    }

    @PutMapping("/film")
    public ResponseEntity<Film> insertFilm(@RequestBody Film film, HttpServletRequest request) {
        UserInfo userInfo = getUserInfoFromRequest(request);
        log.info("User {} inserting film: {}", userInfo.getUsername(), film.getTitle());

        service.insertFilm(film, userInfo.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }
    private UserInfo getUserInfoFromRequest(HttpServletRequest request) {
        UserInfo userInfo = (UserInfo) request.getAttribute("userInfo");

        return userInfo;
    }

    @PatchMapping("/film/{id}")
    public ResponseEntity<Void> modifyFilm(@PathVariable Long id, @RequestBody Film film, HttpServletRequest request) {
        log.info("Modifying film: {}", film.getTitle());
        UserInfo userInfo = (UserInfo) request.getAttribute("userInfo");

        if (!id.equals(film.getId())) {
            return ResponseEntity.badRequest().build();
        }

        service.modifyFilm(film, userInfo.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/filmseen/{id}")
    public ResponseEntity<Void> filmSeen(@PathVariable Long id, @RequestBody Film film) {
        log.info("Modifying seen status for film: {}", film.getTitle());
        if (!id.equals(film.getId())) {
            return ResponseEntity.badRequest().build();
        }
        service.modifyFilmSeen(film);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/film")
    public ResponseEntity<Void> removeFilm(@RequestBody Film film, HttpServletRequest request) {
        log.info("Deleting film: {}", film.getTitle());
        UserInfo userInfo = (UserInfo) request.getAttribute("userInfo");
        service.removeFilm(film, userInfo.getEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/films/all")
    public ResponseEntity<List<Film>> getFilms() {
        log.info("Accessing all films");
        return ResponseEntity.ok(service.getAllFilms());
    }

    @GetMapping("/films")
    public ResponseEntity<Page<Film>> getFilms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Accessing paginated films");

        Sort.Direction direction;
        if (sortDir.equalsIgnoreCase("desc"))
            direction = Sort.Direction.DESC;
        else
            direction = Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "title"));
        Page<Film> filmsPage = service.getFilms(pageable);

        return ResponseEntity.ok(filmsPage);
    }

    @GetMapping("/films/search")
    public ResponseEntity<Page<Film>> searchFilms(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("Searching films: {}", title);

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Sort.Direction direction;
        if (sortDir.equalsIgnoreCase("desc"))
            direction = Sort.Direction.DESC;
        else
            direction = Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "title"));
        Page<Film> filmsPage = service.searchFilmsByTitle(title, pageable);

        return ResponseEntity.ok(filmsPage);
    }

    @GetMapping("/external/{title}")
    public Film getExternalFilm(@PathVariable String title, @RequestParam(required = false) Integer year) {
        return service.getFilmFromExternal(title, year);
    }

    @GetMapping("/getrandomfilm")
    public ResponseEntity<Film> getRandomFilm() {
        log.info("Getting random film");
        Film randomFilm = service.getRandomFilm();
        return ResponseEntity.ok(randomFilm);
    }
}