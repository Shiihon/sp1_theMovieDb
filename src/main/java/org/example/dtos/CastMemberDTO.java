package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.CastMember;
import org.example.entities.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CastMemberDTO {
    private Long id;
    private String name;
    @JsonProperty("known_for_department")
    private String role;
    private String job;
    private List<Long> movieIds;

    public CastMemberDTO(CastMember castMember) {
        this.id = castMember.getId();
        this.name = castMember.getName();
        this.role = castMember.getRole();
        this.job = castMember.getJob();
        this.movieIds = castMember.getMovies().stream().map(Movie::getId).collect(Collectors.toList());
    }

    public CastMemberDTO(Long id, String name, String role, String job) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.job = job;
        this.movieIds = new ArrayList<>();
    }

    public CastMember getAsEntity() {
        return new CastMember(
                id,
                name,
                role,
                job
        );
    }

    public void addMovieDTO(Long movieId) {
        this.movieIds.add(movieId);
    }
}
