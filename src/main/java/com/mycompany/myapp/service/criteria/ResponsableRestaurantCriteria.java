package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.ResponsableRestaurant} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.ResponsableRestaurantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /responsable-restaurants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ResponsableRestaurantCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomResponsable;

    private StringFilter prenomResponsable;

    private StringFilter adresseResponsable;

    private StringFilter numResponsable;

    private Boolean distinct;

    public ResponsableRestaurantCriteria() {}

    public ResponsableRestaurantCriteria(ResponsableRestaurantCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomResponsable = other.nomResponsable == null ? null : other.nomResponsable.copy();
        this.prenomResponsable = other.prenomResponsable == null ? null : other.prenomResponsable.copy();
        this.adresseResponsable = other.adresseResponsable == null ? null : other.adresseResponsable.copy();
        this.numResponsable = other.numResponsable == null ? null : other.numResponsable.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ResponsableRestaurantCriteria copy() {
        return new ResponsableRestaurantCriteria(this);
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

    public StringFilter getNomResponsable() {
        return nomResponsable;
    }

    public StringFilter nomResponsable() {
        if (nomResponsable == null) {
            nomResponsable = new StringFilter();
        }
        return nomResponsable;
    }

    public void setNomResponsable(StringFilter nomResponsable) {
        this.nomResponsable = nomResponsable;
    }

    public StringFilter getPrenomResponsable() {
        return prenomResponsable;
    }

    public StringFilter prenomResponsable() {
        if (prenomResponsable == null) {
            prenomResponsable = new StringFilter();
        }
        return prenomResponsable;
    }

    public void setPrenomResponsable(StringFilter prenomResponsable) {
        this.prenomResponsable = prenomResponsable;
    }

    public StringFilter getAdresseResponsable() {
        return adresseResponsable;
    }

    public StringFilter adresseResponsable() {
        if (adresseResponsable == null) {
            adresseResponsable = new StringFilter();
        }
        return adresseResponsable;
    }

    public void setAdresseResponsable(StringFilter adresseResponsable) {
        this.adresseResponsable = adresseResponsable;
    }

    public StringFilter getNumResponsable() {
        return numResponsable;
    }

    public StringFilter numResponsable() {
        if (numResponsable == null) {
            numResponsable = new StringFilter();
        }
        return numResponsable;
    }

    public void setNumResponsable(StringFilter numResponsable) {
        this.numResponsable = numResponsable;
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
        final ResponsableRestaurantCriteria that = (ResponsableRestaurantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomResponsable, that.nomResponsable) &&
            Objects.equals(prenomResponsable, that.prenomResponsable) &&
            Objects.equals(adresseResponsable, that.adresseResponsable) &&
            Objects.equals(numResponsable, that.numResponsable) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomResponsable, prenomResponsable, adresseResponsable, numResponsable, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ResponsableRestaurantCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomResponsable != null ? "nomResponsable=" + nomResponsable + ", " : "") +
            (prenomResponsable != null ? "prenomResponsable=" + prenomResponsable + ", " : "") +
            (adresseResponsable != null ? "adresseResponsable=" + adresseResponsable + ", " : "") +
            (numResponsable != null ? "numResponsable=" + numResponsable + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
