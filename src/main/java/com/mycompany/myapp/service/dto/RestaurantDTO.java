package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Restaurant} entity.
 */
public class RestaurantDTO implements Serializable {

    private Long id;

    private String nomRestaurant;

    private String adresseRestaurant;

    private String numRestaurant;

    private Instant dateOuverture;

    private Instant dateFermiture;

    private ResponsableRestaurantDTO ResponsableRestaurant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomRestaurant() {
        return nomRestaurant;
    }

    public void setNomRestaurant(String nomRestaurant) {
        this.nomRestaurant = nomRestaurant;
    }

    public String getAdresseRestaurant() {
        return adresseRestaurant;
    }

    public void setAdresseRestaurant(String adresseRestaurant) {
        this.adresseRestaurant = adresseRestaurant;
    }

    public String getNumRestaurant() {
        return numRestaurant;
    }

    public void setNumRestaurant(String numRestaurant) {
        this.numRestaurant = numRestaurant;
    }

    public Instant getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(Instant dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Instant getDateFermiture() {
        return dateFermiture;
    }

    public void setDateFermiture(Instant dateFermiture) {
        this.dateFermiture = dateFermiture;
    }

    public ResponsableRestaurantDTO getResponsableRestaurant() {
        return ResponsableRestaurant;
    }

    public void setResponsableRestaurant(ResponsableRestaurantDTO ResponsableRestaurant) {
        this.ResponsableRestaurant = ResponsableRestaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantDTO)) {
            return false;
        }

        RestaurantDTO restaurantDTO = (RestaurantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, restaurantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantDTO{" +
            "id=" + getId() +
            ", nomRestaurant='" + getNomRestaurant() + "'" +
            ", adresseRestaurant='" + getAdresseRestaurant() + "'" +
            ", numRestaurant='" + getNumRestaurant() + "'" +
            ", dateOuverture='" + getDateOuverture() + "'" +
            ", dateFermiture='" + getDateFermiture() + "'" +
            ", ResponsableRestaurant=" + getResponsableRestaurant() +
            "}";
    }
}
