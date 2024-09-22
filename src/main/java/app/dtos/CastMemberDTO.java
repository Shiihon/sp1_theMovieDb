package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import app.entities.CastMember;
import app.entities.Movie;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CastMemberDTO {
    private Integer id;
    private String name;
    @JsonProperty("known_for_department")
    private String role;
    private String job;
    private Set<Integer> movieIds;

    public CastMemberDTO(CastMember castMember) {
        this.id = castMember.getId();
        this.name = castMember.getName();
        this.role = castMember.getRole();
        this.job = castMember.getJob();
        this.movieIds = castMember.getMovies().stream().map(Movie::getId).collect(Collectors.toSet());
    }

    public CastMemberDTO(Integer id, String name, String role, String job) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.job = job;
        this.movieIds = new HashSet<>();
    }

    public CastMember getAsEntity() {
        return new CastMember(
                id,
                name,
                role,
                job
        );
    }

    public void addMovieId(Integer movieId) {
        if (this.movieIds == null) {
            movieIds = new HashSet<>();
        }

        this.movieIds.add(movieId);
    }
}
