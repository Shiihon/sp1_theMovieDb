package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private Long id;
    private String name;
    private String role;
    private String job;
    @ManyToMany(mappedBy = "cast")
    private List<Movie> movies;

    public CastMember(Long id, String name, String role, String job) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.job = job;
        this.movies = new ArrayList<>();
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
        movie.getCast().add(this);
    }
}
