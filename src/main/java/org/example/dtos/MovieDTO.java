package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.entities.Genre;
import org.example.entities.Movie;

import java.time.LocalDate;
import java.util.ArrayList;
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
    @EqualsAndHashCode.Exclude
    private Double popularity;
    @JsonProperty("release_date")
    private LocalDate releaseDate;
    @JsonProperty("vote_average")
    @EqualsAndHashCode.Exclude
    private Double voteAverage;
    @JsonProperty("genre_ids")
    private List<Long> genreIds = new ArrayList<>();
    private List<CastMemberDTO> cast = new ArrayList<>();

    public MovieDTO(Movie movie) {
        this.id = movie.getId();
        this.originalTitle = movie.getOriginalTitle();
        this.overview = movie.getOverview();
        this.popularity = movie.getPopularity();
        this.releaseDate = movie.getReleaseDate();
        this.voteAverage = movie.getVoteAverage();
        this.genreIds = movie.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        this.cast = movie.getCast().stream().map(CastMemberDTO::new).collect(Collectors.toList());
    }

    public Movie getAsEntity() {
        return new Movie(id,
                originalTitle,
                overview,
                popularity,
                releaseDate,
                voteAverage,
                cast.stream().map(CastMemberDTO::getAsEntity).collect(Collectors.toList())
        );
    }

    public void addCastMemberDTO(CastMemberDTO castMemberDTO) {
        this.cast.add(castMemberDTO);
    }
}
