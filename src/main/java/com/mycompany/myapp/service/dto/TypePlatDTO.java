package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.TypePlat} entity.
 */
public class TypePlatDTO implements Serializable {

    private Long id;

    private String type;

    @Lob
    private String imagePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypePlatDTO)) {
            return false;
        }

        TypePlatDTO typePlatDTO = (TypePlatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, typePlatDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TypePlatDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", imagePath='" + getImagePath() + "'" +
            "}";
    }
}
