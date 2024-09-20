package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "Movie.getAll", query = "SELECT m FROM Movie m"),
        @NamedQuery(name = "Movie.getByAverage", query = "SELECT m FROM Movie m ORDER BY m.voteAverage"),
        @NamedQuery(name = "Movie.getByPopularity", query = "SELECT m FROM Movie m ORDER BY m.voteAverage"),
        @NamedQuery(name = "Movie.getByTitle", query = "SELECT m FROM Movie m WHERE LOWER(m.originalTitle) LIKE LOWER(:originalTitle)"),
        @NamedQuery(name = "Movie.getByGenre", query = "SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
})
@Entity
@Table(name = "movie")
public class Movie {
    @Id
    private Integer id;
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
    @EqualsAndHashCode.Exclude
    private Set<Genre> genres;
    @ManyToMany
    @EqualsAndHashCode.Exclude
    private Set<CastMember> cast;

    public Movie(Integer id, String originalTitle, String overview, Double popularity, LocalDate releaseDate, Double voteAverage, Set<CastMember> cast) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.cast = cast;
        this.genres = new HashSet<>();
    }

    public void addCastMember(CastMember castMember) {
        if (this.cast == null) {
            cast = new HashSet<>();
        }

        this.cast.add(castMember);
        castMember.getMovies().add(this);
    }
}