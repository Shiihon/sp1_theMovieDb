package app.dtos;

import app.entities.Genre;
import app.entities.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {
    private Integer id;
    @JsonProperty("original_title")
    private String originalTitle;
    private String overview;
    @EqualsAndHashCode.Exclude
    private Double popularity;
    @JsonProperty("release_date")
    private LocalDate releaseDate;
    @JsonProperty("vote_average")
    @EqualsAndHashCode.Exclude
    private Double voteAverage;
    @JsonProperty("genre_ids")
    private Set<Integer> genreIds;
    private Set<CastMemberDTO> cast;

    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.originalTitle = movie.getOriginalTitle();
        this.overview = movie.getOverview();
        this.popularity = movie.getPopularity();
        this.releaseDate = movie.getReleaseDate();
        this.voteAverage = movie.getVoteAverage();
        this.genreIds = movie.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        this.cast = movie.getCast().stream().map(CastMemberDTO::new).collect(Collectors.toSet());
    }

    public Movie getAsEntity() {
        return new Movie(
                id,
                originalTitle,
                overview,
                popularity,
                releaseDate,
                voteAverage,
                cast.stream().map(CastMemberDTO::getAsEntity).collect(Collectors.toSet())
        );
    }

    public void addCastMemberDTO(CastMemberDTO castMemberDTO) {
        if (this.cast == null) {
            cast = new HashSet<>();
        }

        this.cast.add(castMemberDTO);
    }

    public void addGenreId(Integer genreId) {
        if (this.genreIds == null) {
            genreIds = new HashSet<>();
        }

        this.genreIds.add(genreId);
    }
}
