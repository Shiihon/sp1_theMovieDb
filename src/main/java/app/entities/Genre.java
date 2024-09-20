package app.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedQueries({
        @NamedQuery(name = "Genre.getById", query = "SELECT g FROM Genre g WHERE g.id = :id")
})
@Entity
@Table(name = "genre")
public class Genre {
    @Id
    private Integer id;
    private String name;
}