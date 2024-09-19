package org.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.GenreDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "genre")
public class Genre {
    @Id
    private Long id;
    private String name;

    public Genre(GenreDTO genreDTO) {
        this.id = genreDTO.getId();
        this.name = genreDTO.getName();
    }
}
