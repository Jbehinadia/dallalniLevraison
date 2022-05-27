package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.ResponsableRestaurant} entity.
 */
public class ResponsableRestaurantDTO implements Serializable {

    private Long id;

    private String nomResponsable;

    private String prenomResponsable;

    private String adresseResponsable;

    private String numResponsable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomResponsable() {
        return nomResponsable;
    }

    public void setNomResponsable(String nomResponsable) {
        this.nomResponsable = nomResponsable;
    }

    public String getPrenomResponsable() {
        return prenomResponsable;
    }

    public void setPrenomResponsable(String prenomResponsable) {
        this.prenomResponsable = prenomResponsable;
    }

    public String getAdresseResponsable() {
        return adresseResponsable;
    }

    public void setAdresseResponsable(String adresseResponsable) {
        this.adresseResponsable = adresseResponsable;
    }

    public String getNumResponsable() {
        return numResponsable;
    }

    public void setNumResponsable(String numResponsable) {
        this.numResponsable = numResponsable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponsableRestaurantDTO)) {
            return false;
        }

        ResponsableRestaurantDTO responsableRestaurantDTO = (ResponsableRestaurantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, responsableRestaurantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResponsableRestaurantDTO{" +
            "id=" + getId() +
            ", nomResponsable='" + getNomResponsable() + "'" +
            ", prenomResponsable='" + getPrenomResponsable() + "'" +
            ", adresseResponsable='" + getAdresseResponsable() + "'" +
            ", numResponsable='" + getNumResponsable() + "'" +
            "}";
    }
}
