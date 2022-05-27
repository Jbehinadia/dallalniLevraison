package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A ResponsableRestaurant.
 */
@Entity
@Table(name = "responsable_restaurant")
public class ResponsableRestaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom_responsable")
    private String nomResponsable;

    @Column(name = "prenom_responsable")
    private String prenomResponsable;

    @Column(name = "adresse_responsable")
    private String adresseResponsable;

    @Column(name = "num_responsable")
    private String numResponsable;

    @JsonIgnoreProperties(value = { "menus" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Restaurant restaurant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ResponsableRestaurant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomResponsable() {
        return this.nomResponsable;
    }

    public ResponsableRestaurant nomResponsable(String nomResponsable) {
        this.setNomResponsable(nomResponsable);
        return this;
    }

    public void setNomResponsable(String nomResponsable) {
        this.nomResponsable = nomResponsable;
    }

    public String getPrenomResponsable() {
        return this.prenomResponsable;
    }

    public ResponsableRestaurant prenomResponsable(String prenomResponsable) {
        this.setPrenomResponsable(prenomResponsable);
        return this;
    }

    public void setPrenomResponsable(String prenomResponsable) {
        this.prenomResponsable = prenomResponsable;
    }

    public String getAdresseResponsable() {
        return this.adresseResponsable;
    }

    public ResponsableRestaurant adresseResponsable(String adresseResponsable) {
        this.setAdresseResponsable(adresseResponsable);
        return this;
    }

    public void setAdresseResponsable(String adresseResponsable) {
        this.adresseResponsable = adresseResponsable;
    }

    public String getNumResponsable() {
        return this.numResponsable;
    }

    public ResponsableRestaurant numResponsable(String numResponsable) {
        this.setNumResponsable(numResponsable);
        return this;
    }

    public void setNumResponsable(String numResponsable) {
        this.numResponsable = numResponsable;
    }

    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public ResponsableRestaurant restaurant(Restaurant restaurant) {
        this.setRestaurant(restaurant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResponsableRestaurant)) {
            return false;
        }
        return id != null && id.equals(((ResponsableRestaurant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResponsableRestaurant{" +
            "id=" + getId() +
            ", nomResponsable='" + getNomResponsable() + "'" +
            ", prenomResponsable='" + getPrenomResponsable() + "'" +
            ", adresseResponsable='" + getAdresseResponsable() + "'" +
            ", numResponsable='" + getNumResponsable() + "'" +
            "}";
    }
}
