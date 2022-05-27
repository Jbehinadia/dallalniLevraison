package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "prenom_client")
    private String prenomClient;

    @Column(name = "adresse_client")
    private String adresseClient;

    @Column(name = "num_client")
    private String numClient;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Client id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomClient() {
        return this.nomClient;
    }

    public Client nomClient(String nomClient) {
        this.setNomClient(nomClient);
        return this;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getPrenomClient() {
        return this.prenomClient;
    }

    public Client prenomClient(String prenomClient) {
        this.setPrenomClient(prenomClient);
        return this;
    }

    public void setPrenomClient(String prenomClient) {
        this.prenomClient = prenomClient;
    }

    public String getAdresseClient() {
        return this.adresseClient;
    }

    public Client adresseClient(String adresseClient) {
        this.setAdresseClient(adresseClient);
        return this;
    }

    public void setAdresseClient(String adresseClient) {
        this.adresseClient = adresseClient;
    }

    public String getNumClient() {
        return this.numClient;
    }

    public Client numClient(String numClient) {
        this.setNumClient(numClient);
        return this;
    }

    public void setNumClient(String numClient) {
        this.numClient = numClient;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return id != null && id.equals(((Client) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Client{" +
            "id=" + getId() +
            ", nomClient='" + getNomClient() + "'" +
            ", prenomClient='" + getPrenomClient() + "'" +
            ", adresseClient='" + getAdresseClient() + "'" +
            ", numClient='" + getNumClient() + "'" +
            "}";
    }
}
