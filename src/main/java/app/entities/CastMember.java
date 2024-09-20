package app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "CastMember.getAll", query = "SELECT c FROM CastMember c")
})
@Entity
@Table(name = "cast_member")
public class CastMember {
    @Id
    private Integer id;
    private String name;
    private String role;
    private String job;
    @ManyToMany(mappedBy = "cast")
    @EqualsAndHashCode.Exclude
    private Set<Movie> movies;

    public CastMember(Integer id, String name, String role, String job) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.job = job;
        this.movies = new HashSet<>();
    }

    public void addMovie(Movie movie) {
        if (this.movies == null) {
            movies = new HashSet<>();
        }

        movies.add(movie);
        movie.getCast().add(this);
    }
}
