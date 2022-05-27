package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Plat} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.PlatResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /plats?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PlatCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomPlat;

    private DoubleFilter prix;

    private DoubleFilter remisePerc;

    private DoubleFilter remiceVal;

    private LongFilter menuId;

    private LongFilter typePlatId;

    private Boolean distinct;

    public PlatCriteria() {}

    public PlatCriteria(PlatCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomPlat = other.nomPlat == null ? null : other.nomPlat.copy();
        this.prix = other.prix == null ? null : other.prix.copy();
        this.remisePerc = other.remisePerc == null ? null : other.remisePerc.copy();
        this.remiceVal = other.remiceVal == null ? null : other.remiceVal.copy();
        this.menuId = other.menuId == null ? null : other.menuId.copy();
        this.typePlatId = other.typePlatId == null ? null : other.typePlatId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PlatCriteria copy() {
        return new PlatCriteria(this);
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

    public StringFilter getNomPlat() {
        return nomPlat;
    }

    public StringFilter nomPlat() {
        if (nomPlat == null) {
            nomPlat = new StringFilter();
        }
        return nomPlat;
    }

    public void setNomPlat(StringFilter nomPlat) {
        this.nomPlat = nomPlat;
    }

    public DoubleFilter getPrix() {
        return prix;
    }

    public DoubleFilter prix() {
        if (prix == null) {
            prix = new DoubleFilter();
        }
        return prix;
    }

    public void setPrix(DoubleFilter prix) {
        this.prix = prix;
    }

    public DoubleFilter getRemisePerc() {
        return remisePerc;
    }

    public DoubleFilter remisePerc() {
        if (remisePerc == null) {
            remisePerc = new DoubleFilter();
        }
        return remisePerc;
    }

    public void setRemisePerc(DoubleFilter remisePerc) {
        this.remisePerc = remisePerc;
    }

    public DoubleFilter getRemiceVal() {
        return remiceVal;
    }

    public DoubleFilter remiceVal() {
        if (remiceVal == null) {
            remiceVal = new DoubleFilter();
        }
        return remiceVal;
    }

    public void setRemiceVal(DoubleFilter remiceVal) {
        this.remiceVal = remiceVal;
    }

    public LongFilter getMenuId() {
        return menuId;
    }

    public LongFilter menuId() {
        if (menuId == null) {
            menuId = new LongFilter();
        }
        return menuId;
    }

    public void setMenuId(LongFilter menuId) {
        this.menuId = menuId;
    }

    public LongFilter getTypePlatId() {
        return typePlatId;
    }

    public LongFilter typePlatId() {
        if (typePlatId == null) {
            typePlatId = new LongFilter();
        }
        return typePlatId;
    }

    public void setTypePlatId(LongFilter typePlatId) {
        this.typePlatId = typePlatId;
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
        final PlatCriteria that = (PlatCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomPlat, that.nomPlat) &&
            Objects.equals(prix, that.prix) &&
            Objects.equals(remisePerc, that.remisePerc) &&
            Objects.equals(remiceVal, that.remiceVal) &&
            Objects.equals(menuId, that.menuId) &&
            Objects.equals(typePlatId, that.typePlatId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomPlat, prix, remisePerc, remiceVal, menuId, typePlatId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlatCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomPlat != null ? "nomPlat=" + nomPlat + ", " : "") +
            (prix != null ? "prix=" + prix + ", " : "") +
            (remisePerc != null ? "remisePerc=" + remisePerc + ", " : "") +
            (remiceVal != null ? "remiceVal=" + remiceVal + ", " : "") +
            (menuId != null ? "menuId=" + menuId + ", " : "") +
            (typePlatId != null ? "typePlatId=" + typePlatId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
