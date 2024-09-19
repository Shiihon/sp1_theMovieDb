package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.entities.Movie;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {
    private Long id;
    @JsonProperty("original_title")
    private String originalTitle;
    private String overview;
    private Double popularity;
    @JsonProperty("release_date")
    private LocalDate releaseDate;
    @JsonProperty("vote_average")
    @EqualsAndHashCode.Exclude
    private Double voteAverage;
    private List<GenreDTO> genres;
    private List<CastMemberDTO> cast;

    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.originalTitle = movie.getOriginalTitle();
        this.overview = movie.getOverview();
        this.popularity = movie.getPopularity();
        this.releaseDate = movie.getReleaseDate();
        this.voteAverage = movie.getVoteAverage();
        this.genres = movie.getGenres().stream().map(GenreDTO::new).collect(Collectors.toList());
        this.cast = movie.getCast().stream().map(CastMemberDTO::new).collect(Collectors.toList());
    }

    public Movie getAsEntity() {
        return new Movie(id,
                originalTitle,
                overview,
                popularity,
                releaseDate,
                voteAverage,
                genres.stream().map(GenreDTO::getAsEntity).collect(Collectors.toList()),
                cast.stream().map(CastMemberDTO::getAsEntity).collect(Collectors.toList())
        );
    }

    public void addCastMemberDTO(CastMemberDTO castMemberDTO) {
        this.cast.add(castMemberDTO);
    }
}
