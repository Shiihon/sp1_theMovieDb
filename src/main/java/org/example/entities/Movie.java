package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Movie {
    @Id
    private Long id;
    private String originalTitle;
    private String overview;
    private LocalDate releaseDate;
    private Double voteAverage;
    @ManyToMany
    private List<Genre> genres;
}
