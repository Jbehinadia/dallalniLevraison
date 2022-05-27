package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.CommandeDetails} entity.
 */
public class CommandeDetailsDTO implements Serializable {

    private Long id;

    private Double prix;

    private String etat;

    private Double qte;

    private CommandeDTO commande;

    private PlatDTO plat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Double getQte() {
        return qte;
    }

    public void setQte(Double qte) {
        this.qte = qte;
    }

    public CommandeDTO getCommande() {
        return commande;
    }

    public void setCommande(CommandeDTO commande) {
        this.commande = commande;
    }

    public PlatDTO getPlat() {
        return plat;
    }

    public void setPlat(PlatDTO plat) {
        this.plat = plat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandeDetailsDTO)) {
            return false;
        }

        CommandeDetailsDTO commandeDetailsDTO = (CommandeDetailsDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, commandeDetailsDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommandeDetailsDTO{" +
            "id=" + getId() +
            ", prix=" + getPrix() +
            ", etat='" + getEtat() + "'" +
            ", qte=" + getQte() +
            ", commande=" + getCommande() +
            ", plat=" + getPlat() +
            "}";
    }
}
