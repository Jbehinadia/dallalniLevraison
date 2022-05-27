package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Commande} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.CommandeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /commandes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CommandeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter adresseCommande;

    private StringFilter etat;

    private InstantFilter dateCommande;

    private DoubleFilter prixTotal;

    private DoubleFilter remisePerc;

    private DoubleFilter remiceVal;

    private DoubleFilter prixLivreson;

    private InstantFilter dateSortie;

    private LongFilter livreurId;

    private LongFilter clientId;

    private Boolean distinct;

    public CommandeCriteria() {}

    public CommandeCriteria(CommandeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.adresseCommande = other.adresseCommande == null ? null : other.adresseCommande.copy();
        this.etat = other.etat == null ? null : other.etat.copy();
        this.dateCommande = other.dateCommande == null ? null : other.dateCommande.copy();
        this.prixTotal = other.prixTotal == null ? null : other.prixTotal.copy();
        this.remisePerc = other.remisePerc == null ? null : other.remisePerc.copy();
        this.remiceVal = other.remiceVal == null ? null : other.remiceVal.copy();
        this.prixLivreson = other.prixLivreson == null ? null : other.prixLivreson.copy();
        this.dateSortie = other.dateSortie == null ? null : other.dateSortie.copy();
        this.livreurId = other.livreurId == null ? null : other.livreurId.copy();
        this.clientId = other.clientId == null ? null : other.clientId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CommandeCriteria copy() {
        return new CommandeCriteria(this);
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

    public StringFilter getAdresseCommande() {
        return adresseCommande;
    }

    public StringFilter adresseCommande() {
        if (adresseCommande == null) {
            adresseCommande = new StringFilter();
        }
        return adresseCommande;
    }

    public void setAdresseCommande(StringFilter adresseCommande) {
        this.adresseCommande = adresseCommande;
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

    public InstantFilter getDateCommande() {
        return dateCommande;
    }

    public InstantFilter dateCommande() {
        if (dateCommande == null) {
            dateCommande = new InstantFilter();
        }
        return dateCommande;
    }

    public void setDateCommande(InstantFilter dateCommande) {
        this.dateCommande = dateCommande;
    }

    public DoubleFilter getPrixTotal() {
        return prixTotal;
    }

    public DoubleFilter prixTotal() {
        if (prixTotal == null) {
            prixTotal = new DoubleFilter();
        }
        return prixTotal;
    }

    public void setPrixTotal(DoubleFilter prixTotal) {
        this.prixTotal = prixTotal;
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

    public DoubleFilter getPrixLivreson() {
        return prixLivreson;
    }

    public DoubleFilter prixLivreson() {
        if (prixLivreson == null) {
            prixLivreson = new DoubleFilter();
        }
        return prixLivreson;
    }

    public void setPrixLivreson(DoubleFilter prixLivreson) {
        this.prixLivreson = prixLivreson;
    }

    public InstantFilter getDateSortie() {
        return dateSortie;
    }

    public InstantFilter dateSortie() {
        if (dateSortie == null) {
            dateSortie = new InstantFilter();
        }
        return dateSortie;
    }

    public void setDateSortie(InstantFilter dateSortie) {
        this.dateSortie = dateSortie;
    }

    public LongFilter getLivreurId() {
        return livreurId;
    }

    public LongFilter livreurId() {
        if (livreurId == null) {
            livreurId = new LongFilter();
        }
        return livreurId;
    }

    public void setLivreurId(LongFilter livreurId) {
        this.livreurId = livreurId;
    }

    public LongFilter getClientId() {
        return clientId;
    }

    public LongFilter clientId() {
        if (clientId == null) {
            clientId = new LongFilter();
        }
        return clientId;
    }

    public void setClientId(LongFilter clientId) {
        this.clientId = clientId;
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
        final CommandeCriteria that = (CommandeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(adresseCommande, that.adresseCommande) &&
            Objects.equals(etat, that.etat) &&
            Objects.equals(dateCommande, that.dateCommande) &&
            Objects.equals(prixTotal, that.prixTotal) &&
            Objects.equals(remisePerc, that.remisePerc) &&
            Objects.equals(remiceVal, that.remiceVal) &&
            Objects.equals(prixLivreson, that.prixLivreson) &&
            Objects.equals(dateSortie, that.dateSortie) &&
            Objects.equals(livreurId, that.livreurId) &&
            Objects.equals(clientId, that.clientId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            adresseCommande,
            etat,
            dateCommande,
            prixTotal,
            remisePerc,
            remiceVal,
            prixLivreson,
            dateSortie,
            livreurId,
            clientId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommandeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (adresseCommande != null ? "adresseCommande=" + adresseCommande + ", " : "") +
            (etat != null ? "etat=" + etat + ", " : "") +
            (dateCommande != null ? "dateCommande=" + dateCommande + ", " : "") +
            (prixTotal != null ? "prixTotal=" + prixTotal + ", " : "") +
            (remisePerc != null ? "remisePerc=" + remisePerc + ", " : "") +
            (remiceVal != null ? "remiceVal=" + remiceVal + ", " : "") +
            (prixLivreson != null ? "prixLivreson=" + prixLivreson + ", " : "") +
            (dateSortie != null ? "dateSortie=" + dateSortie + ", " : "") +
            (livreurId != null ? "livreurId=" + livreurId + ", " : "") +
            (clientId != null ? "clientId=" + clientId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
