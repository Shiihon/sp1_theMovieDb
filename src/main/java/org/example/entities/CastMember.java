package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.CastMemberDTO;

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

    public CastMember(CastMemberDTO castMemberDTO) {
        this.id = castMemberDTO.getId();
        this.name = castMemberDTO.getName();
        this.role = castMemberDTO.getRole();
        this.job = castMemberDTO.getJob();
    }
}
