package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    @ManyToMany
    private List<CastMember> cast;

    public void addCastMember(CastMember castMember) {
        this.cast.add(castMember);
        castMember.getMovies().add(this);
    }
}
