package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Plat.
 */
@Entity
@Table(name = "plat")
public class Plat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom_plat")
    private String nomPlat;

    @Lob
    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "prix")
    private Double prix;

    @Column(name = "remise_perc")
    private Double remisePerc;

    @Column(name = "remice_val")
    private Double remiceVal;

    @OneToMany(mappedBy = "plat")
    @JsonIgnoreProperties(value = { "commande", "plat" }, allowSetters = true)
    private Set<CommandeDetails> commandeDetails = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "plats", "restaurant" }, allowSetters = true)
    private Menu menu;

    @ManyToOne
    @JsonIgnoreProperties(value = { "plats" }, allowSetters = true)
    private TypePlat typePlat;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Plat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomPlat() {
        return this.nomPlat;
    }

    public Plat nomPlat(String nomPlat) {
        this.setNomPlat(nomPlat);
        return this;
    }

    public void setNomPlat(String nomPlat) {
        this.nomPlat = nomPlat;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public Plat imagePath(String imagePath) {
        this.setImagePath(imagePath);
        return this;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Double getPrix() {
        return this.prix;
    }

    public Plat prix(Double prix) {
        this.setPrix(prix);
        return this;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public Double getRemisePerc() {
        return this.remisePerc;
    }

    public Plat remisePerc(Double remisePerc) {
        this.setRemisePerc(remisePerc);
        return this;
    }

    public void setRemisePerc(Double remisePerc) {
        this.remisePerc = remisePerc;
    }

    public Double getRemiceVal() {
        return this.remiceVal;
    }

    public Plat remiceVal(Double remiceVal) {
        this.setRemiceVal(remiceVal);
        return this;
    }

    public void setRemiceVal(Double remiceVal) {
        this.remiceVal = remiceVal;
    }

    public Set<CommandeDetails> getCommandeDetails() {
        return this.commandeDetails;
    }

    public void setCommandeDetails(Set<CommandeDetails> commandeDetails) {
        if (this.commandeDetails != null) {
            this.commandeDetails.forEach(i -> i.setPlat(null));
        }
        if (commandeDetails != null) {
            commandeDetails.forEach(i -> i.setPlat(this));
        }
        this.commandeDetails = commandeDetails;
    }

    public Plat commandeDetails(Set<CommandeDetails> commandeDetails) {
        this.setCommandeDetails(commandeDetails);
        return this;
    }

    public Plat addCommandeDetails(CommandeDetails commandeDetails) {
        this.commandeDetails.add(commandeDetails);
        commandeDetails.setPlat(this);
        return this;
    }

    public Plat removeCommandeDetails(CommandeDetails commandeDetails) {
        this.commandeDetails.remove(commandeDetails);
        commandeDetails.setPlat(null);
        return this;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Plat menu(Menu menu) {
        this.setMenu(menu);
        return this;
    }

    public TypePlat getTypePlat() {
        return this.typePlat;
    }

    public void setTypePlat(TypePlat typePlat) {
        this.typePlat = typePlat;
    }

    public Plat typePlat(TypePlat typePlat) {
        this.setTypePlat(typePlat);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Plat)) {
            return false;
        }
        return id != null && id.equals(((Plat) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Plat{" +
            "id=" + getId() +
            ", nomPlat='" + getNomPlat() + "'" +
            ", imagePath='" + getImagePath() + "'" +
            ", prix=" + getPrix() +
            ", remisePerc=" + getRemisePerc() +
            ", remiceVal=" + getRemiceVal() +
            "}";
    }
}
