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
 * Criteria class for the {@link com.mycompany.myapp.domain.Menu} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.MenuResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /menus?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class MenuCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomMenu;

    private LongFilter restaurantId;

    private Boolean distinct;

    public MenuCriteria() {}

    public MenuCriteria(MenuCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomMenu = other.nomMenu == null ? null : other.nomMenu.copy();
        this.restaurantId = other.restaurantId == null ? null : other.restaurantId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MenuCriteria copy() {
        return new MenuCriteria(this);
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

    public StringFilter getNomMenu() {
        return nomMenu;
    }

    public StringFilter nomMenu() {
        if (nomMenu == null) {
            nomMenu = new StringFilter();
        }
        return nomMenu;
    }

    public void setNomMenu(StringFilter nomMenu) {
        this.nomMenu = nomMenu;
    }

    public LongFilter getRestaurantId() {
        return restaurantId;
    }

    public LongFilter restaurantId() {
        if (restaurantId == null) {
            restaurantId = new LongFilter();
        }
        return restaurantId;
    }

    public void setRestaurantId(LongFilter restaurantId) {
        this.restaurantId = restaurantId;
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
        final MenuCriteria that = (MenuCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomMenu, that.nomMenu) &&
            Objects.equals(restaurantId, that.restaurantId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomMenu, restaurantId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MenuCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomMenu != null ? "nomMenu=" + nomMenu + ", " : "") +
            (restaurantId != null ? "restaurantId=" + restaurantId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
