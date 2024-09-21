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
    @Column(unique = true, nullable = false)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String role;
    private String job;
    @ManyToMany(mappedBy = "cast")
    @EqualsAndHashCode.Exclude
    @Column(nullable = false)
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
