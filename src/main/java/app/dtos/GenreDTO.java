package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import app.entities.Genre;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenreDTO {
    private Integer id;
    private String name;

    public GenreDTO(Genre genre) {
        this.id = genre.getId();
        this.name = genre.getName();
    }

    public Genre getAsEntity() {
        return new Genre(
                id,
                name
        );
    }
}
