package org.example.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.CastMember;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CastMemberDTO {
    private Long id;
    private String name;
    @JsonProperty("known_for_department")
    private String role;
    private String job;

    public CastMemberDTO(CastMember castMember) {
        this.id = castMember.getId();
        this.name = castMember.getName();
        this.role = castMember.getRole();
        this.job = castMember.getJob();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CastMemberDTO that = (CastMemberDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(role, that.role) && Objects.equals(job, that.job);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, role, job);
    }
}
