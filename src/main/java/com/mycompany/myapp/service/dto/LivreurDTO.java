package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Livreur} entity.
 */
public class LivreurDTO implements Serializable {

    private Long id;

    private String nomLivreur;

    private String prenomLivreur;

    private String adresseLivreur;

    private String numLivreur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomLivreur() {
        return nomLivreur;
    }

    public void setNomLivreur(String nomLivreur) {
        this.nomLivreur = nomLivreur;
    }

    public String getPrenomLivreur() {
        return prenomLivreur;
    }

    public void setPrenomLivreur(String prenomLivreur) {
        this.prenomLivreur = prenomLivreur;
    }

    public String getAdresseLivreur() {
        return adresseLivreur;
    }

    public void setAdresseLivreur(String adresseLivreur) {
        this.adresseLivreur = adresseLivreur;
    }

    public String getNumLivreur() {
        return numLivreur;
    }

    public void setNumLivreur(String numLivreur) {
        this.numLivreur = numLivreur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LivreurDTO)) {
            return false;
        }

        LivreurDTO livreurDTO = (LivreurDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, livreurDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LivreurDTO{" +
            "id=" + getId() +
            ", nomLivreur='" + getNomLivreur() + "'" +
            ", prenomLivreur='" + getPrenomLivreur() + "'" +
            ", adresseLivreur='" + getAdresseLivreur() + "'" +
            ", numLivreur='" + getNumLivreur() + "'" +
            "}";
    }
}
