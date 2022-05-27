package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A CommandeDetails.
 */
@Entity
@Table(name = "commande_details")
public class CommandeDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "prix")
    private Double prix;

    @Column(name = "etat")
    private String etat;

    @Column(name = "qte")
    private Double qte;

    @ManyToOne
    @JsonIgnoreProperties(value = { "livreur", "client" }, allowSetters = true)
    private Commande commande;

    @ManyToOne
    @JsonIgnoreProperties(value = { "menu", "typePlat" }, allowSetters = true)
    private Plat plat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CommandeDetails id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrix() {
        return this.prix;
    }

    public CommandeDetails prix(Double prix) {
        this.setPrix(prix);
        return this;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public String getEtat() {
        return this.etat;
    }

    public CommandeDetails etat(String etat) {
        this.setEtat(etat);
        return this;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Double getQte() {
        return this.qte;
    }

    public CommandeDetails qte(Double qte) {
        this.setQte(qte);
        return this;
    }

    public void setQte(Double qte) {
        this.qte = qte;
    }

    public Commande getCommande() {
        return this.commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public CommandeDetails commande(Commande commande) {
        this.setCommande(commande);
        return this;
    }

    public Plat getPlat() {
        return this.plat;
    }

    public void setPlat(Plat plat) {
        this.plat = plat;
    }

    public CommandeDetails plat(Plat plat) {
        this.setPlat(plat);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandeDetails)) {
            return false;
        }
        return id != null && id.equals(((CommandeDetails) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommandeDetails{" +
            "id=" + getId() +
            ", prix=" + getPrix() +
            ", etat='" + getEtat() + "'" +
            ", qte=" + getQte() +
            "}";
    }
}
