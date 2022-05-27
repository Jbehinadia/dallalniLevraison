package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Menu.
 */
@Entity
@Table(name = "menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom_menu")
    private String nomMenu;

    @OneToMany(mappedBy = "menu")
    @JsonIgnoreProperties(value = { "commandeDetails", "menu", "typePlat" }, allowSetters = true)
    private Set<Plat> plats = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "menus" }, allowSetters = true)
    private Restaurant restaurant;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Menu id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomMenu() {
        return this.nomMenu;
    }

    public Menu nomMenu(String nomMenu) {
        this.setNomMenu(nomMenu);
        return this;
    }

    public void setNomMenu(String nomMenu) {
        this.nomMenu = nomMenu;
    }

    public Set<Plat> getPlats() {
        return this.plats;
    }

    public void setPlats(Set<Plat> plats) {
        if (this.plats != null) {
            this.plats.forEach(i -> i.setMenu(null));
        }
        if (plats != null) {
            plats.forEach(i -> i.setMenu(this));
        }
        this.plats = plats;
    }

    public Menu plats(Set<Plat> plats) {
        this.setPlats(plats);
        return this;
    }

    public Menu addPlat(Plat plat) {
        this.plats.add(plat);
        plat.setMenu(this);
        return this;
    }

    public Menu removePlat(Plat plat) {
        this.plats.remove(plat);
        plat.setMenu(null);
        return this;
    }

    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Menu restaurant(Restaurant restaurant) {
        this.setRestaurant(restaurant);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Menu)) {
            return false;
        }
        return id != null && id.equals(((Menu) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Menu{" +
            "id=" + getId() +
            ", nomMenu='" + getNomMenu() + "'" +
            "}";
    }
}
