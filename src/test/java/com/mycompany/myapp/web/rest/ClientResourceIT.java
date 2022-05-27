package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Client;
import com.mycompany.myapp.repository.ClientRepository;
import com.mycompany.myapp.service.dto.ClientDTO;
import com.mycompany.myapp.service.mapper.ClientMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ClientResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ClientResourceIT {

    private static final String DEFAULT_NOM_CLIENT = "AAAAAAAAAA";
    private static final String UPDATED_NOM_CLIENT = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM_CLIENT = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM_CLIENT = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE_CLIENT = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE_CLIENT = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_CLIENT = "AAAAAAAAAA";
    private static final String UPDATED_NUM_CLIENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/clients";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restClientMockMvc;

    private Client client;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createEntity(EntityManager em) {
        Client client = new Client()
            .nomClient(DEFAULT_NOM_CLIENT)
            .prenomClient(DEFAULT_PRENOM_CLIENT)
            .adresseClient(DEFAULT_ADRESSE_CLIENT)
            .numClient(DEFAULT_NUM_CLIENT);
        return client;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Client createUpdatedEntity(EntityManager em) {
        Client client = new Client()
            .nomClient(UPDATED_NOM_CLIENT)
            .prenomClient(UPDATED_PRENOM_CLIENT)
            .adresseClient(UPDATED_ADRESSE_CLIENT)
            .numClient(UPDATED_NUM_CLIENT);
        return client;
    }

    @BeforeEach
    public void initTest() {
        client = createEntity(em);
    }

    @Test
    @Transactional
    void createClient() throws Exception {
        int databaseSizeBeforeCreate = clientRepository.findAll().size();
        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);
        restClientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeCreate + 1);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getNomClient()).isEqualTo(DEFAULT_NOM_CLIENT);
        assertThat(testClient.getPrenomClient()).isEqualTo(DEFAULT_PRENOM_CLIENT);
        assertThat(testClient.getAdresseClient()).isEqualTo(DEFAULT_ADRESSE_CLIENT);
        assertThat(testClient.getNumClient()).isEqualTo(DEFAULT_NUM_CLIENT);
    }

    @Test
    @Transactional
    void createClientWithExistingId() throws Exception {
        // Create the Client with an existing ID
        client.setId(1L);
        ClientDTO clientDTO = clientMapper.toDto(client);

        int databaseSizeBeforeCreate = clientRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restClientMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllClients() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomClient").value(hasItem(DEFAULT_NOM_CLIENT)))
            .andExpect(jsonPath("$.[*].prenomClient").value(hasItem(DEFAULT_PRENOM_CLIENT)))
            .andExpect(jsonPath("$.[*].adresseClient").value(hasItem(DEFAULT_ADRESSE_CLIENT)))
            .andExpect(jsonPath("$.[*].numClient").value(hasItem(DEFAULT_NUM_CLIENT)));
    }

    @Test
    @Transactional
    void getClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get the client
        restClientMockMvc
            .perform(get(ENTITY_API_URL_ID, client.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(client.getId().intValue()))
            .andExpect(jsonPath("$.nomClient").value(DEFAULT_NOM_CLIENT))
            .andExpect(jsonPath("$.prenomClient").value(DEFAULT_PRENOM_CLIENT))
            .andExpect(jsonPath("$.adresseClient").value(DEFAULT_ADRESSE_CLIENT))
            .andExpect(jsonPath("$.numClient").value(DEFAULT_NUM_CLIENT));
    }

    @Test
    @Transactional
    void getClientsByIdFiltering() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        Long id = client.getId();

        defaultClientShouldBeFound("id.equals=" + id);
        defaultClientShouldNotBeFound("id.notEquals=" + id);

        defaultClientShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultClientShouldNotBeFound("id.greaterThan=" + id);

        defaultClientShouldBeFound("id.lessThanOrEqual=" + id);
        defaultClientShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllClientsByNomClientIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nomClient equals to DEFAULT_NOM_CLIENT
        defaultClientShouldBeFound("nomClient.equals=" + DEFAULT_NOM_CLIENT);

        // Get all the clientList where nomClient equals to UPDATED_NOM_CLIENT
        defaultClientShouldNotBeFound("nomClient.equals=" + UPDATED_NOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByNomClientIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nomClient not equals to DEFAULT_NOM_CLIENT
        defaultClientShouldNotBeFound("nomClient.notEquals=" + DEFAULT_NOM_CLIENT);

        // Get all the clientList where nomClient not equals to UPDATED_NOM_CLIENT
        defaultClientShouldBeFound("nomClient.notEquals=" + UPDATED_NOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByNomClientIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nomClient in DEFAULT_NOM_CLIENT or UPDATED_NOM_CLIENT
        defaultClientShouldBeFound("nomClient.in=" + DEFAULT_NOM_CLIENT + "," + UPDATED_NOM_CLIENT);

        // Get all the clientList where nomClient equals to UPDATED_NOM_CLIENT
        defaultClientShouldNotBeFound("nomClient.in=" + UPDATED_NOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByNomClientIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nomClient is not null
        defaultClientShouldBeFound("nomClient.specified=true");

        // Get all the clientList where nomClient is null
        defaultClientShouldNotBeFound("nomClient.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByNomClientContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nomClient contains DEFAULT_NOM_CLIENT
        defaultClientShouldBeFound("nomClient.contains=" + DEFAULT_NOM_CLIENT);

        // Get all the clientList where nomClient contains UPDATED_NOM_CLIENT
        defaultClientShouldNotBeFound("nomClient.contains=" + UPDATED_NOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByNomClientNotContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where nomClient does not contain DEFAULT_NOM_CLIENT
        defaultClientShouldNotBeFound("nomClient.doesNotContain=" + DEFAULT_NOM_CLIENT);

        // Get all the clientList where nomClient does not contain UPDATED_NOM_CLIENT
        defaultClientShouldBeFound("nomClient.doesNotContain=" + UPDATED_NOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByPrenomClientIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where prenomClient equals to DEFAULT_PRENOM_CLIENT
        defaultClientShouldBeFound("prenomClient.equals=" + DEFAULT_PRENOM_CLIENT);

        // Get all the clientList where prenomClient equals to UPDATED_PRENOM_CLIENT
        defaultClientShouldNotBeFound("prenomClient.equals=" + UPDATED_PRENOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByPrenomClientIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where prenomClient not equals to DEFAULT_PRENOM_CLIENT
        defaultClientShouldNotBeFound("prenomClient.notEquals=" + DEFAULT_PRENOM_CLIENT);

        // Get all the clientList where prenomClient not equals to UPDATED_PRENOM_CLIENT
        defaultClientShouldBeFound("prenomClient.notEquals=" + UPDATED_PRENOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByPrenomClientIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where prenomClient in DEFAULT_PRENOM_CLIENT or UPDATED_PRENOM_CLIENT
        defaultClientShouldBeFound("prenomClient.in=" + DEFAULT_PRENOM_CLIENT + "," + UPDATED_PRENOM_CLIENT);

        // Get all the clientList where prenomClient equals to UPDATED_PRENOM_CLIENT
        defaultClientShouldNotBeFound("prenomClient.in=" + UPDATED_PRENOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByPrenomClientIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where prenomClient is not null
        defaultClientShouldBeFound("prenomClient.specified=true");

        // Get all the clientList where prenomClient is null
        defaultClientShouldNotBeFound("prenomClient.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByPrenomClientContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where prenomClient contains DEFAULT_PRENOM_CLIENT
        defaultClientShouldBeFound("prenomClient.contains=" + DEFAULT_PRENOM_CLIENT);

        // Get all the clientList where prenomClient contains UPDATED_PRENOM_CLIENT
        defaultClientShouldNotBeFound("prenomClient.contains=" + UPDATED_PRENOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByPrenomClientNotContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where prenomClient does not contain DEFAULT_PRENOM_CLIENT
        defaultClientShouldNotBeFound("prenomClient.doesNotContain=" + DEFAULT_PRENOM_CLIENT);

        // Get all the clientList where prenomClient does not contain UPDATED_PRENOM_CLIENT
        defaultClientShouldBeFound("prenomClient.doesNotContain=" + UPDATED_PRENOM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByAdresseClientIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where adresseClient equals to DEFAULT_ADRESSE_CLIENT
        defaultClientShouldBeFound("adresseClient.equals=" + DEFAULT_ADRESSE_CLIENT);

        // Get all the clientList where adresseClient equals to UPDATED_ADRESSE_CLIENT
        defaultClientShouldNotBeFound("adresseClient.equals=" + UPDATED_ADRESSE_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByAdresseClientIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where adresseClient not equals to DEFAULT_ADRESSE_CLIENT
        defaultClientShouldNotBeFound("adresseClient.notEquals=" + DEFAULT_ADRESSE_CLIENT);

        // Get all the clientList where adresseClient not equals to UPDATED_ADRESSE_CLIENT
        defaultClientShouldBeFound("adresseClient.notEquals=" + UPDATED_ADRESSE_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByAdresseClientIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where adresseClient in DEFAULT_ADRESSE_CLIENT or UPDATED_ADRESSE_CLIENT
        defaultClientShouldBeFound("adresseClient.in=" + DEFAULT_ADRESSE_CLIENT + "," + UPDATED_ADRESSE_CLIENT);

        // Get all the clientList where adresseClient equals to UPDATED_ADRESSE_CLIENT
        defaultClientShouldNotBeFound("adresseClient.in=" + UPDATED_ADRESSE_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByAdresseClientIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where adresseClient is not null
        defaultClientShouldBeFound("adresseClient.specified=true");

        // Get all the clientList where adresseClient is null
        defaultClientShouldNotBeFound("adresseClient.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByAdresseClientContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where adresseClient contains DEFAULT_ADRESSE_CLIENT
        defaultClientShouldBeFound("adresseClient.contains=" + DEFAULT_ADRESSE_CLIENT);

        // Get all the clientList where adresseClient contains UPDATED_ADRESSE_CLIENT
        defaultClientShouldNotBeFound("adresseClient.contains=" + UPDATED_ADRESSE_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByAdresseClientNotContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where adresseClient does not contain DEFAULT_ADRESSE_CLIENT
        defaultClientShouldNotBeFound("adresseClient.doesNotContain=" + DEFAULT_ADRESSE_CLIENT);

        // Get all the clientList where adresseClient does not contain UPDATED_ADRESSE_CLIENT
        defaultClientShouldBeFound("adresseClient.doesNotContain=" + UPDATED_ADRESSE_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByNumClientIsEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where numClient equals to DEFAULT_NUM_CLIENT
        defaultClientShouldBeFound("numClient.equals=" + DEFAULT_NUM_CLIENT);

        // Get all the clientList where numClient equals to UPDATED_NUM_CLIENT
        defaultClientShouldNotBeFound("numClient.equals=" + UPDATED_NUM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByNumClientIsNotEqualToSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where numClient not equals to DEFAULT_NUM_CLIENT
        defaultClientShouldNotBeFound("numClient.notEquals=" + DEFAULT_NUM_CLIENT);

        // Get all the clientList where numClient not equals to UPDATED_NUM_CLIENT
        defaultClientShouldBeFound("numClient.notEquals=" + UPDATED_NUM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByNumClientIsInShouldWork() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where numClient in DEFAULT_NUM_CLIENT or UPDATED_NUM_CLIENT
        defaultClientShouldBeFound("numClient.in=" + DEFAULT_NUM_CLIENT + "," + UPDATED_NUM_CLIENT);

        // Get all the clientList where numClient equals to UPDATED_NUM_CLIENT
        defaultClientShouldNotBeFound("numClient.in=" + UPDATED_NUM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByNumClientIsNullOrNotNull() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where numClient is not null
        defaultClientShouldBeFound("numClient.specified=true");

        // Get all the clientList where numClient is null
        defaultClientShouldNotBeFound("numClient.specified=false");
    }

    @Test
    @Transactional
    void getAllClientsByNumClientContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where numClient contains DEFAULT_NUM_CLIENT
        defaultClientShouldBeFound("numClient.contains=" + DEFAULT_NUM_CLIENT);

        // Get all the clientList where numClient contains UPDATED_NUM_CLIENT
        defaultClientShouldNotBeFound("numClient.contains=" + UPDATED_NUM_CLIENT);
    }

    @Test
    @Transactional
    void getAllClientsByNumClientNotContainsSomething() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        // Get all the clientList where numClient does not contain DEFAULT_NUM_CLIENT
        defaultClientShouldNotBeFound("numClient.doesNotContain=" + DEFAULT_NUM_CLIENT);

        // Get all the clientList where numClient does not contain UPDATED_NUM_CLIENT
        defaultClientShouldBeFound("numClient.doesNotContain=" + UPDATED_NUM_CLIENT);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultClientShouldBeFound(String filter) throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(client.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomClient").value(hasItem(DEFAULT_NOM_CLIENT)))
            .andExpect(jsonPath("$.[*].prenomClient").value(hasItem(DEFAULT_PRENOM_CLIENT)))
            .andExpect(jsonPath("$.[*].adresseClient").value(hasItem(DEFAULT_ADRESSE_CLIENT)))
            .andExpect(jsonPath("$.[*].numClient").value(hasItem(DEFAULT_NUM_CLIENT)));

        // Check, that the count call also returns 1
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultClientShouldNotBeFound(String filter) throws Exception {
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restClientMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingClient() throws Exception {
        // Get the client
        restClientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // Update the client
        Client updatedClient = clientRepository.findById(client.getId()).get();
        // Disconnect from session so that the updates on updatedClient are not directly saved in db
        em.detach(updatedClient);
        updatedClient
            .nomClient(UPDATED_NOM_CLIENT)
            .prenomClient(UPDATED_PRENOM_CLIENT)
            .adresseClient(UPDATED_ADRESSE_CLIENT)
            .numClient(UPDATED_NUM_CLIENT);
        ClientDTO clientDTO = clientMapper.toDto(updatedClient);

        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getNomClient()).isEqualTo(UPDATED_NOM_CLIENT);
        assertThat(testClient.getPrenomClient()).isEqualTo(UPDATED_PRENOM_CLIENT);
        assertThat(testClient.getAdresseClient()).isEqualTo(UPDATED_ADRESSE_CLIENT);
        assertThat(testClient.getNumClient()).isEqualTo(UPDATED_NUM_CLIENT);
    }

    @Test
    @Transactional
    void putNonExistingClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, clientDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateClientWithPatch() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient.nomClient(UPDATED_NOM_CLIENT).prenomClient(UPDATED_PRENOM_CLIENT).adresseClient(UPDATED_ADRESSE_CLIENT);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getNomClient()).isEqualTo(UPDATED_NOM_CLIENT);
        assertThat(testClient.getPrenomClient()).isEqualTo(UPDATED_PRENOM_CLIENT);
        assertThat(testClient.getAdresseClient()).isEqualTo(UPDATED_ADRESSE_CLIENT);
        assertThat(testClient.getNumClient()).isEqualTo(DEFAULT_NUM_CLIENT);
    }

    @Test
    @Transactional
    void fullUpdateClientWithPatch() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeUpdate = clientRepository.findAll().size();

        // Update the client using partial update
        Client partialUpdatedClient = new Client();
        partialUpdatedClient.setId(client.getId());

        partialUpdatedClient
            .nomClient(UPDATED_NOM_CLIENT)
            .prenomClient(UPDATED_PRENOM_CLIENT)
            .adresseClient(UPDATED_ADRESSE_CLIENT)
            .numClient(UPDATED_NUM_CLIENT);

        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedClient.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedClient))
            )
            .andExpect(status().isOk());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
        Client testClient = clientList.get(clientList.size() - 1);
        assertThat(testClient.getNomClient()).isEqualTo(UPDATED_NOM_CLIENT);
        assertThat(testClient.getPrenomClient()).isEqualTo(UPDATED_PRENOM_CLIENT);
        assertThat(testClient.getAdresseClient()).isEqualTo(UPDATED_ADRESSE_CLIENT);
        assertThat(testClient.getNumClient()).isEqualTo(UPDATED_NUM_CLIENT);
    }

    @Test
    @Transactional
    void patchNonExistingClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, clientDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamClient() throws Exception {
        int databaseSizeBeforeUpdate = clientRepository.findAll().size();
        client.setId(count.incrementAndGet());

        // Create the Client
        ClientDTO clientDTO = clientMapper.toDto(client);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restClientMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(clientDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Client in the database
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteClient() throws Exception {
        // Initialize the database
        clientRepository.saveAndFlush(client);

        int databaseSizeBeforeDelete = clientRepository.findAll().size();

        // Delete the client
        restClientMockMvc
            .perform(delete(ENTITY_API_URL_ID, client.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Client> clientList = clientRepository.findAll();
        assertThat(clientList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
