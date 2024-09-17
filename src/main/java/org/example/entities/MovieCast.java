package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "movie_cast")
public class MovieCast {
    @Id
    private Long id;
    @OneToOne
    private Movie movie;
    @OneToMany
    private List<CastMember> cast;

}
