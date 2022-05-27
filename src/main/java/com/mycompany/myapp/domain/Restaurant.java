package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;

/**
 * A Restaurant.
 */
@Entity
@Table(name = "restaurant")
public class Restaurant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom_restaurant")
    private String nomRestaurant;

    @Column(name = "adresse_restaurant")
    private String adresseRestaurant;

    @Column(name = "num_restaurant")
    private String numRestaurant;

    @Column(name = "date_ouverture")
    private Instant dateOuverture;

    @Column(name = "date_fermiture")
    private Instant dateFermiture;

    @ManyToOne
    private ResponsableRestaurant responsableRestaurant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Restaurant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomRestaurant() {
        return this.nomRestaurant;
    }

    public Restaurant nomRestaurant(String nomRestaurant) {
        this.setNomRestaurant(nomRestaurant);
        return this;
    }

    public void setNomRestaurant(String nomRestaurant) {
        this.nomRestaurant = nomRestaurant;
    }

    public String getAdresseRestaurant() {
        return this.adresseRestaurant;
    }

    public Restaurant adresseRestaurant(String adresseRestaurant) {
        this.setAdresseRestaurant(adresseRestaurant);
        return this;
    }

    public void setAdresseRestaurant(String adresseRestaurant) {
        this.adresseRestaurant = adresseRestaurant;
    }

    public String getNumRestaurant() {
        return this.numRestaurant;
    }

    public Restaurant numRestaurant(String numRestaurant) {
        this.setNumRestaurant(numRestaurant);
        return this;
    }

    public void setNumRestaurant(String numRestaurant) {
        this.numRestaurant = numRestaurant;
    }

    public Instant getDateOuverture() {
        return this.dateOuverture;
    }

    public Restaurant dateOuverture(Instant dateOuverture) {
        this.setDateOuverture(dateOuverture);
        return this;
    }

    public void setDateOuverture(Instant dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public Instant getDateFermiture() {
        return this.dateFermiture;
    }

    public Restaurant dateFermiture(Instant dateFermiture) {
        this.setDateFermiture(dateFermiture);
        return this;
    }

    public void setDateFermiture(Instant dateFermiture) {
        this.dateFermiture = dateFermiture;
    }

    public ResponsableRestaurant getResponsableRestaurant() {
        return this.responsableRestaurant;
    }

    public void setResponsableRestaurant(ResponsableRestaurant ResponsableRestaurant) {
        this.responsableRestaurant = ResponsableRestaurant;
    }

    public Restaurant responsableRestaurant(ResponsableRestaurant ResponsableRestaurant) {
        this.setResponsableRestaurant(ResponsableRestaurant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Restaurant)) {
            return false;
        }
        return id != null && id.equals(((Restaurant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Restaurant{" +
            "id=" + getId() +
            ", nomRestaurant='" + getNomRestaurant() + "'" +
            ", adresseRestaurant='" + getAdresseRestaurant() + "'" +
            ", numRestaurant='" + getNumRestaurant() + "'" +
            ", dateOuverture='" + getDateOuverture() + "'" +
            ", dateFermiture='" + getDateFermiture() + "'" +
            "}";
    }
}
