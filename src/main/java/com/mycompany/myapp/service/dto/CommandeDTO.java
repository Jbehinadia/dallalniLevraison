package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Commande} entity.
 */
public class CommandeDTO implements Serializable {

    private Long id;

    private String adresseCommande;

    private String etat;

    private Instant dateCommande;

    private Double prixTotal;

    private Double remisePerc;

    private Double remiceVal;

    private Double prixLivreson;

    private Instant dateSortie;

    private LivreurDTO livreur;

    private ClientDTO client;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdresseCommande() {
        return adresseCommande;
    }

    public void setAdresseCommande(String adresseCommande) {
        this.adresseCommande = adresseCommande;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Instant getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Instant dateCommande) {
        this.dateCommande = dateCommande;
    }

    public Double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(Double prixTotal) {
        this.prixTotal = prixTotal;
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

    public Double getPrixLivreson() {
        return prixLivreson;
    }

    public void setPrixLivreson(Double prixLivreson) {
        this.prixLivreson = prixLivreson;
    }

    public Instant getDateSortie() {
        return dateSortie;
    }

    public void setDateSortie(Instant dateSortie) {
        this.dateSortie = dateSortie;
    }

    public LivreurDTO getLivreur() {
        return livreur;
    }

    public void setLivreur(LivreurDTO livreur) {
        this.livreur = livreur;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandeDTO)) {
            return false;
        }

        CommandeDTO commandeDTO = (CommandeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, commandeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommandeDTO{" +
            "id=" + getId() +
            ", adresseCommande='" + getAdresseCommande() + "'" +
            ", etat='" + getEtat() + "'" +
            ", dateCommande='" + getDateCommande() + "'" +
            ", prixTotal=" + getPrixTotal() +
            ", remisePerc=" + getRemisePerc() +
            ", remiceVal=" + getRemiceVal() +
            ", prixLivreson=" + getPrixLivreson() +
            ", dateSortie='" + getDateSortie() + "'" +
            ", livreur=" + getLivreur() +
            ", client=" + getClient() +
            "}";
    }
}
