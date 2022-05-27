package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Restaurant} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.RestaurantResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /restaurants?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class RestaurantCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomRestaurant;

    private StringFilter adresseRestaurant;

    private StringFilter numRestaurant;

    private InstantFilter dateOuverture;

    private InstantFilter dateFermiture;

    private LongFilter responsableRestaurantId;

    private Boolean distinct;

    public RestaurantCriteria() {}

    public RestaurantCriteria(RestaurantCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomRestaurant = other.nomRestaurant == null ? null : other.nomRestaurant.copy();
        this.adresseRestaurant = other.adresseRestaurant == null ? null : other.adresseRestaurant.copy();
        this.numRestaurant = other.numRestaurant == null ? null : other.numRestaurant.copy();
        this.dateOuverture = other.dateOuverture == null ? null : other.dateOuverture.copy();
        this.dateFermiture = other.dateFermiture == null ? null : other.dateFermiture.copy();
        this.responsableRestaurantId = other.responsableRestaurantId == null ? null : other.responsableRestaurantId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RestaurantCriteria copy() {
        return new RestaurantCriteria(this);
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

    public StringFilter getNomRestaurant() {
        return nomRestaurant;
    }

    public StringFilter nomRestaurant() {
        if (nomRestaurant == null) {
            nomRestaurant = new StringFilter();
        }
        return nomRestaurant;
    }

    public void setNomRestaurant(StringFilter nomRestaurant) {
        this.nomRestaurant = nomRestaurant;
    }

    public StringFilter getAdresseRestaurant() {
        return adresseRestaurant;
    }

    public StringFilter adresseRestaurant() {
        if (adresseRestaurant == null) {
            adresseRestaurant = new StringFilter();
        }
        return adresseRestaurant;
    }

    public void setAdresseRestaurant(StringFilter adresseRestaurant) {
        this.adresseRestaurant = adresseRestaurant;
    }

    public StringFilter getNumRestaurant() {
        return numRestaurant;
    }

    public StringFilter numRestaurant() {
        if (numRestaurant == null) {
            numRestaurant = new StringFilter();
        }
        return numRestaurant;
    }

    public void setNumRestaurant(StringFilter numRestaurant) {
        this.numRestaurant = numRestaurant;
    }

    public InstantFilter getDateOuverture() {
        return dateOuverture;
    }

    public InstantFilter dateOuverture() {
        if (dateOuverture == null) {
            dateOuverture = new InstantFilter();
        }
        return dateOuverture;
    }

    public void setDateOuverture(InstantFilter dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public InstantFilter getDateFermiture() {
        return dateFermiture;
    }

    public InstantFilter dateFermiture() {
        if (dateFermiture == null) {
            dateFermiture = new InstantFilter();
        }
        return dateFermiture;
    }

    public void setDateFermiture(InstantFilter dateFermiture) {
        this.dateFermiture = dateFermiture;
    }

    public LongFilter getResponsableRestaurantId() {
        return responsableRestaurantId;
    }

    public LongFilter responsableRestaurantId() {
        if (responsableRestaurantId == null) {
            responsableRestaurantId = new LongFilter();
        }
        return responsableRestaurantId;
    }

    public void setResponsableRestaurantId(LongFilter responsableRestaurantId) {
        this.responsableRestaurantId = responsableRestaurantId;
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
        final RestaurantCriteria that = (RestaurantCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomRestaurant, that.nomRestaurant) &&
            Objects.equals(adresseRestaurant, that.adresseRestaurant) &&
            Objects.equals(numRestaurant, that.numRestaurant) &&
            Objects.equals(dateOuverture, that.dateOuverture) &&
            Objects.equals(dateFermiture, that.dateFermiture) &&
            Objects.equals(responsableRestaurantId, that.responsableRestaurantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            nomRestaurant,
            adresseRestaurant,
            numRestaurant,
            dateOuverture,
            dateFermiture,
            responsableRestaurantId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomRestaurant != null ? "nomRestaurant=" + nomRestaurant + ", " : "") +
            (adresseRestaurant != null ? "adresseRestaurant=" + adresseRestaurant + ", " : "") +
            (numRestaurant != null ? "numRestaurant=" + numRestaurant + ", " : "") +
            (dateOuverture != null ? "dateOuverture=" + dateOuverture + ", " : "") +
            (dateFermiture != null ? "dateFermiture=" + dateFermiture + ", " : "") +
            (responsableRestaurantId != null ? "responsableRestaurantId=" + responsableRestaurantId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
