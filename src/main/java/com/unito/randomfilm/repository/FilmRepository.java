package com.unito.randomfilm.repository;

import com.unito.randomfilm.entity.Film;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {
    boolean existsByTitle(String title);
    Optional<Film> findByTitleIgnoreCase(String title);
    //void deleteByTitle(String title);
    //List<Film> findBySeenFalse();

    @Query("SELECT f.id FROM Film f WHERE f.seen=false")
    List<Long> findUnseenFilmIds();

    Page<Film> findByTitleContainingIgnoreCase(String title, Pageable pageable);


    @Modifying
    @Query("UPDATE Film f SET f.seen = :seen WHERE f.id = :id")
    void updateSeenStatus(boolean seen, Long id);





}
