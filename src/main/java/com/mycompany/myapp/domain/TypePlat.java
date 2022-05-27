package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A TypePlat.
 */
@Entity
@Table(name = "type_plat")
public class TypePlat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "image_path")
    private String imagePath;

    @OneToMany(mappedBy = "typePlat")
    @JsonIgnoreProperties(value = { "commandeDetails", "menu", "typePlat" }, allowSetters = true)
    private Set<Plat> plats = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TypePlat id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public TypePlat type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public TypePlat imagePath(String imagePath) {
        this.setImagePath(imagePath);
        return this;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Set<Plat> getPlats() {
        return this.plats;
    }

    public void setPlats(Set<Plat> plats) {
        if (this.plats != null) {
            this.plats.forEach(i -> i.setTypePlat(null));
        }
        if (plats != null) {
            plats.forEach(i -> i.setTypePlat(this));
        }
        this.plats = plats;
    }

    public TypePlat plats(Set<Plat> plats) {
        this.setPlats(plats);
        return this;
    }

    public TypePlat addPlat(Plat plat) {
        this.plats.add(plat);
        plat.setTypePlat(this);
        return this;
    }

    public TypePlat removePlat(Plat plat) {
        this.plats.remove(plat);
        plat.setTypePlat(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypePlat)) {
            return false;
        }
        return id != null && id.equals(((TypePlat) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TypePlat{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", imagePath='" + getImagePath() + "'" +
            "}";
    }
}
