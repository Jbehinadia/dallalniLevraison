package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Livreur} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.LivreurResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /livreurs?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class LivreurCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomLivreur;

    private StringFilter prenomLivreur;

    private StringFilter adresseLivreur;

    private StringFilter numLivreur;

    private Boolean distinct;

    public LivreurCriteria() {}

    public LivreurCriteria(LivreurCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomLivreur = other.nomLivreur == null ? null : other.nomLivreur.copy();
        this.prenomLivreur = other.prenomLivreur == null ? null : other.prenomLivreur.copy();
        this.adresseLivreur = other.adresseLivreur == null ? null : other.adresseLivreur.copy();
        this.numLivreur = other.numLivreur == null ? null : other.numLivreur.copy();
        this.distinct = other.distinct;
    }

    @Override
    public LivreurCriteria copy() {
        return new LivreurCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getNomLivreur() {
        return nomLivreur;
    }

    public StringFilter nomLivreur() {
        if (nomLivreur == null) {
            nomLivreur = new StringFilter();
        }
        return nomLivreur;
    }

    public void setNomLivreur(StringFilter nomLivreur) {
        this.nomLivreur = nomLivreur;
    }

    public StringFilter getPrenomLivreur() {
        return prenomLivreur;
    }

    public StringFilter prenomLivreur() {
        if (prenomLivreur == null) {
            prenomLivreur = new StringFilter();
        }
        return prenomLivreur;
    }

    public void setPrenomLivreur(StringFilter prenomLivreur) {
        this.prenomLivreur = prenomLivreur;
    }

    public StringFilter getAdresseLivreur() {
        return adresseLivreur;
    }

    public StringFilter adresseLivreur() {
        if (adresseLivreur == null) {
            adresseLivreur = new StringFilter();
        }
        return adresseLivreur;
    }

    public void setAdresseLivreur(StringFilter adresseLivreur) {
        this.adresseLivreur = adresseLivreur;
    }

    public StringFilter getNumLivreur() {
        return numLivreur;
    }

    public StringFilter numLivreur() {
        if (numLivreur == null) {
            numLivreur = new StringFilter();
        }
        return numLivreur;
    }

    public void setNumLivreur(StringFilter numLivreur) {
        this.numLivreur = numLivreur;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LivreurCriteria that = (LivreurCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomLivreur, that.nomLivreur) &&
            Objects.equals(prenomLivreur, that.prenomLivreur) &&
            Objects.equals(adresseLivreur, that.adresseLivreur) &&
            Objects.equals(numLivreur, that.numLivreur) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomLivreur, prenomLivreur, adresseLivreur, numLivreur, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LivreurCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomLivreur != null ? "nomLivreur=" + nomLivreur + ", " : "") +
            (prenomLivreur != null ? "prenomLivreur=" + prenomLivreur + ", " : "") +
            (adresseLivreur != null ? "adresseLivreur=" + adresseLivreur + ", " : "") +
            (numLivreur != null ? "numLivreur=" + numLivreur + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
