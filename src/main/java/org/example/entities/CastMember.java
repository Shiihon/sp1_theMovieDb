package org.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dtos.CastMemberDTO;

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

    public CastMember(CastMemberDTO castMemberDTO) {
        this.id = castMemberDTO.getId();
        this.name = castMemberDTO.getName();
        this.role = castMemberDTO.getRole();
        this.job = castMemberDTO.getJob();
    }
}
