package com.unito.randomfilm.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank
//    @Schema(description = "Titolo del film", example = "Inception")
    private String title;

//    @Schema(description = "Anno uscita del film", example = "2010")
    private Integer year;

//    @Schema(description = "Regista del film", example = "Christopher Nolan")
    private String director;

//    @Schema(description = "Durata del film", example = "148 min")
    private String duration;

//    @Schema(description = "Genere del film", example = "Action, Adventure, Sci-Fi")
    private String genre;

    private Boolean seen;

    private String poster;

    @Column(nullable = false)
    private String userEmail;

    public Film() {}


    public Film(String title, Integer year, String duration, String director, String genre, String poster) {
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.director = director;
        this.genre = genre;
        this.poster=poster;
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String toString() {
        return "Film{" +
                "id='" + id + '\'' +
                "title='" + title + '\'' +
                ", year=" + year +
                ", director='" + director + '\'' +
                ", duration='" + duration + '\'' +
                ", genre='" + genre + '\'' +
                ", seen='" + seen + '\'' +
                ", poster='" + poster + '\'' +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }

}
