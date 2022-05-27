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
 * Criteria class for the {@link com.mycompany.myapp.domain.CommandeDetails} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.CommandeDetailsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /commande-details?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CommandeDetailsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter prix;

    private StringFilter etat;

    private DoubleFilter qte;

    private LongFilter commandeId;

    private LongFilter platId;

    private Boolean distinct;

    public CommandeDetailsCriteria() {}

    public CommandeDetailsCriteria(CommandeDetailsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.prix = other.prix == null ? null : other.prix.copy();
        this.etat = other.etat == null ? null : other.etat.copy();
        this.qte = other.qte == null ? null : other.qte.copy();
        this.commandeId = other.commandeId == null ? null : other.commandeId.copy();
        this.platId = other.platId == null ? null : other.platId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CommandeDetailsCriteria copy() {
        return new CommandeDetailsCriteria(this);
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

    public StringFilter getEtat() {
        return etat;
    }

    public StringFilter etat() {
        if (etat == null) {
            etat = new StringFilter();
        }
        return etat;
    }

    public void setEtat(StringFilter etat) {
        this.etat = etat;
    }

    public DoubleFilter getQte() {
        return qte;
    }

    public DoubleFilter qte() {
        if (qte == null) {
            qte = new DoubleFilter();
        }
        return qte;
    }

    public void setQte(DoubleFilter qte) {
        this.qte = qte;
    }

    public LongFilter getCommandeId() {
        return commandeId;
    }

    public LongFilter commandeId() {
        if (commandeId == null) {
            commandeId = new LongFilter();
        }
        return commandeId;
    }

    public void setCommandeId(LongFilter commandeId) {
        this.commandeId = commandeId;
    }

    public LongFilter getPlatId() {
        return platId;
    }

    public LongFilter platId() {
        if (platId == null) {
            platId = new LongFilter();
        }
        return platId;
    }

    public void setPlatId(LongFilter platId) {
        this.platId = platId;
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
        final CommandeDetailsCriteria that = (CommandeDetailsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(prix, that.prix) &&
            Objects.equals(etat, that.etat) &&
            Objects.equals(qte, that.qte) &&
            Objects.equals(commandeId, that.commandeId) &&
            Objects.equals(platId, that.platId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prix, etat, qte, commandeId, platId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommandeDetailsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (prix != null ? "prix=" + prix + ", " : "") +
            (etat != null ? "etat=" + etat + ", " : "") +
            (qte != null ? "qte=" + qte + ", " : "") +
            (commandeId != null ? "commandeId=" + commandeId + ", " : "") +
            (platId != null ? "platId=" + platId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
