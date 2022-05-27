package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A Livreur.
 */
@Entity
@Table(name = "livreur")
public class Livreur implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom_livreur")
    private String nomLivreur;

    @Column(name = "prenom_livreur")
    private String prenomLivreur;

    @Column(name = "adresse_livreur")
    private String adresseLivreur;

    @Column(name = "num_livreur")
    private String numLivreur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Livreur id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomLivreur() {
        return this.nomLivreur;
    }

    public Livreur nomLivreur(String nomLivreur) {
        this.setNomLivreur(nomLivreur);
        return this;
    }

    public void setNomLivreur(String nomLivreur) {
        this.nomLivreur = nomLivreur;
    }

    public String getPrenomLivreur() {
        return this.prenomLivreur;
    }

    public Livreur prenomLivreur(String prenomLivreur) {
        this.setPrenomLivreur(prenomLivreur);
        return this;
    }

    public void setPrenomLivreur(String prenomLivreur) {
        this.prenomLivreur = prenomLivreur;
    }

    public String getAdresseLivreur() {
        return this.adresseLivreur;
    }

    public Livreur adresseLivreur(String adresseLivreur) {
        this.setAdresseLivreur(adresseLivreur);
        return this;
    }

    public void setAdresseLivreur(String adresseLivreur) {
        this.adresseLivreur = adresseLivreur;
    }

    public String getNumLivreur() {
        return this.numLivreur;
    }

    public Livreur numLivreur(String numLivreur) {
        this.setNumLivreur(numLivreur);
        return this;
    }

    public void setNumLivreur(String numLivreur) {
        this.numLivreur = numLivreur;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Livreur)) {
            return false;
        }
        return id != null && id.equals(((Livreur) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Livreur{" +
            "id=" + getId() +
            ", nomLivreur='" + getNomLivreur() + "'" +
            ", prenomLivreur='" + getPrenomLivreur() + "'" +
            ", adresseLivreur='" + getAdresseLivreur() + "'" +
            ", numLivreur='" + getNumLivreur() + "'" +
            "}";
    }
}
