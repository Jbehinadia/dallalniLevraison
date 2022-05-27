package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Client} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.ClientResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /clients?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ClientCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter nomClient;

    private StringFilter prenomClient;

    private StringFilter adresseClient;

    private StringFilter numClient;

    private Boolean distinct;

    public ClientCriteria() {}

    public ClientCriteria(ClientCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.nomClient = other.nomClient == null ? null : other.nomClient.copy();
        this.prenomClient = other.prenomClient == null ? null : other.prenomClient.copy();
        this.adresseClient = other.adresseClient == null ? null : other.adresseClient.copy();
        this.numClient = other.numClient == null ? null : other.numClient.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ClientCriteria copy() {
        return new ClientCriteria(this);
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

    public StringFilter getNomClient() {
        return nomClient;
    }

    public StringFilter nomClient() {
        if (nomClient == null) {
            nomClient = new StringFilter();
        }
        return nomClient;
    }

    public void setNomClient(StringFilter nomClient) {
        this.nomClient = nomClient;
    }

    public StringFilter getPrenomClient() {
        return prenomClient;
    }

    public StringFilter prenomClient() {
        if (prenomClient == null) {
            prenomClient = new StringFilter();
        }
        return prenomClient;
    }

    public void setPrenomClient(StringFilter prenomClient) {
        this.prenomClient = prenomClient;
    }

    public StringFilter getAdresseClient() {
        return adresseClient;
    }

    public StringFilter adresseClient() {
        if (adresseClient == null) {
            adresseClient = new StringFilter();
        }
        return adresseClient;
    }

    public void setAdresseClient(StringFilter adresseClient) {
        this.adresseClient = adresseClient;
    }

    public StringFilter getNumClient() {
        return numClient;
    }

    public StringFilter numClient() {
        if (numClient == null) {
            numClient = new StringFilter();
        }
        return numClient;
    }

    public void setNumClient(StringFilter numClient) {
        this.numClient = numClient;
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
        final ClientCriteria that = (ClientCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(nomClient, that.nomClient) &&
            Objects.equals(prenomClient, that.prenomClient) &&
            Objects.equals(adresseClient, that.adresseClient) &&
            Objects.equals(numClient, that.numClient) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomClient, prenomClient, adresseClient, numClient, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClientCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (nomClient != null ? "nomClient=" + nomClient + ", " : "") +
            (prenomClient != null ? "prenomClient=" + prenomClient + ", " : "") +
            (adresseClient != null ? "adresseClient=" + adresseClient + ", " : "") +
            (numClient != null ? "numClient=" + numClient + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
