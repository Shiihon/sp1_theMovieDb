package app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "Movie.getAll", query = "SELECT m FROM Movie m"),
        @NamedQuery(name = "Movie.getByAverageDESC", query = "SELECT m FROM Movie m ORDER BY m.voteAverage DESC"),
        @NamedQuery(name = "Movie.getByAverageASC", query = "SELECT m FROM Movie m ORDER BY m.voteAverage"),
        @NamedQuery(name = "Movie.getByPopularity", query = "SELECT m FROM Movie m ORDER BY m.voteAverage"),
        @NamedQuery(name = "Movie.getByTitle", query = "SELECT m FROM Movie m WHERE LOWER(m.originalTitle) LIKE LOWER(:originalTitle)"),
        @NamedQuery(name = "Movie.getByGenre", query = "SELECT m FROM Movie m JOIN m.genres g WHERE g.id = :genreId")
})
@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @Column(unique = true, nullable = false)
    private Integer id;
    @Column(name = "original_title", nullable = false)
    private String originalTitle;
    @Column(length = 1024)
    private String overview;
    @Column(nullable = false)
    private Double popularity;
    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;
    @Column(name = "vote_average", nullable = false)
    private Double voteAverage;
    @ManyToMany
    private Set<Genre> genres;
    @ManyToMany
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