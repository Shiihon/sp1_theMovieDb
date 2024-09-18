package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.MovieDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movie")
public class Movie {
    @Id
    private Long id;
    @Column(name = "original_title")
    private String originalTitle;
    @Column(length = 1024)
    private String overview;
    private Double popularity;
    @Column(name = "release_date")
    private LocalDate releaseDate;
    @Column(name = "vote_average")
    private Double voteAverage;
    @ManyToMany
    private List<Genre> genres;

    public Movie(MovieDTO movieDTO) {
        this.id = movieDTO.getId();
        this.originalTitle = movieDTO.getOriginalTitle();
        this.overview = movieDTO.getOverview();
        this.popularity = movieDTO.getPopularity();
        this.releaseDate = movieDTO.getReleaseDate();
        this.voteAverage = movieDTO.getVoteAverage();
        this.genres = movieDTO.getGenres().stream().map(Genre::new).collect(Collectors.toList());
    }
}
