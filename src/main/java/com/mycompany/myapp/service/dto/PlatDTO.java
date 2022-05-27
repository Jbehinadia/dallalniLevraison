package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Plat} entity.
 */
public class PlatDTO implements Serializable {

    private Long id;

    private String nomPlat;

    @Lob
    private String imagePath;

    private Double prix;

    private Double remisePerc;

    private Double remiceVal;

    private MenuDTO menu;

    private TypePlatDTO typePlat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomPlat() {
        return nomPlat;
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Double getRemisePerc() {
        return remisePerc;
    }

    public void setRemisePerc(Double remisePerc) {
        this.remisePerc = remisePerc;
    }

    public Double getRemiceVal() {
        return remiceVal;
    }

    public void setRemiceVal(Double remiceVal) {
        this.remiceVal = remiceVal;
    }

    public MenuDTO getMenu() {
        return menu;
    }

    public void setMenu(MenuDTO menu) {
        this.menu = menu;
    }

    public TypePlatDTO getTypePlat() {
        return typePlat;
    }

    public void setTypePlat(TypePlatDTO typePlat) {
        this.typePlat = typePlat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlatDTO)) {
            return false;
        }

        PlatDTO platDTO = (PlatDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, platDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlatDTO{" +
            "id=" + getId() +
            ", nomPlat='" + getNomPlat() + "'" +
            ", imagePath='" + getImagePath() + "'" +
            ", prix=" + getPrix() +
            ", remisePerc=" + getRemisePerc() +
            ", remiceVal=" + getRemiceVal() +
            ", menu=" + getMenu() +
            ", typePlat=" + getTypePlat() +
            "}";
    }
}
