package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Commande;
import com.mycompany.myapp.domain.CommandeDetails;
import com.mycompany.myapp.domain.Plat;
import com.mycompany.myapp.repository.CommandeDetailsRepository;
import com.mycompany.myapp.service.dto.CommandeDetailsDTO;
import com.mycompany.myapp.service.mapper.CommandeDetailsMapper;
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
 * Integration tests for the {@link CommandeDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommandeDetailsResourceIT {

    private static final Double DEFAULT_PRIX = 1D;
    private static final Double UPDATED_PRIX = 2D;
    private static final Double SMALLER_PRIX = 1D - 1D;

    private static final String DEFAULT_ETAT = "AAAAAAAAAA";
    private static final String UPDATED_ETAT = "BBBBBBBBBB";

    private static final Double DEFAULT_QTE = 1D;
    private static final Double UPDATED_QTE = 2D;
    private static final Double SMALLER_QTE = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/commande-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommandeDetailsRepository commandeDetailsRepository;

    @Autowired
    private CommandeDetailsMapper commandeDetailsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommandeDetailsMockMvc;

    private CommandeDetails commandeDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommandeDetails createEntity(EntityManager em) {
        CommandeDetails commandeDetails = new CommandeDetails().prix(DEFAULT_PRIX).etat(DEFAULT_ETAT).qte(DEFAULT_QTE);
        return commandeDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommandeDetails createUpdatedEntity(EntityManager em) {
        CommandeDetails commandeDetails = new CommandeDetails().prix(UPDATED_PRIX).etat(UPDATED_ETAT).qte(UPDATED_QTE);
        return commandeDetails;
    }

    @BeforeEach
    public void initTest() {
        commandeDetails = createEntity(em);
    }

    @Test
    @Transactional
    void createCommandeDetails() throws Exception {
        int databaseSizeBeforeCreate = commandeDetailsRepository.findAll().size();
        // Create the CommandeDetails
        CommandeDetailsDTO commandeDetailsDTO = commandeDetailsMapper.toDto(commandeDetails);
        restCommandeDetailsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDetailsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        CommandeDetails testCommandeDetails = commandeDetailsList.get(commandeDetailsList.size() - 1);
        assertThat(testCommandeDetails.getPrix()).isEqualTo(DEFAULT_PRIX);
        assertThat(testCommandeDetails.getEtat()).isEqualTo(DEFAULT_ETAT);
        assertThat(testCommandeDetails.getQte()).isEqualTo(DEFAULT_QTE);
    }

    @Test
    @Transactional
    void createCommandeDetailsWithExistingId() throws Exception {
        // Create the CommandeDetails with an existing ID
        commandeDetails.setId(1L);
        CommandeDetailsDTO commandeDetailsDTO = commandeDetailsMapper.toDto(commandeDetails);

        int databaseSizeBeforeCreate = commandeDetailsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommandeDetailsMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCommandeDetails() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList
        restCommandeDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commandeDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(DEFAULT_PRIX.doubleValue())))
            .andExpect(jsonPath("$.[*].etat").value(hasItem(DEFAULT_ETAT)))
            .andExpect(jsonPath("$.[*].qte").value(hasItem(DEFAULT_QTE.doubleValue())));
    }

    @Test
    @Transactional
    void getCommandeDetails() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get the commandeDetails
        restCommandeDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, commandeDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(commandeDetails.getId().intValue()))
            .andExpect(jsonPath("$.prix").value(DEFAULT_PRIX.doubleValue()))
            .andExpect(jsonPath("$.etat").value(DEFAULT_ETAT))
            .andExpect(jsonPath("$.qte").value(DEFAULT_QTE.doubleValue()));
    }

    @Test
    @Transactional
    void getCommandeDetailsByIdFiltering() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        Long id = commandeDetails.getId();

        defaultCommandeDetailsShouldBeFound("id.equals=" + id);
        defaultCommandeDetailsShouldNotBeFound("id.notEquals=" + id);

        defaultCommandeDetailsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCommandeDetailsShouldNotBeFound("id.greaterThan=" + id);

        defaultCommandeDetailsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCommandeDetailsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByPrixIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where prix equals to DEFAULT_PRIX
        defaultCommandeDetailsShouldBeFound("prix.equals=" + DEFAULT_PRIX);

        // Get all the commandeDetailsList where prix equals to UPDATED_PRIX
        defaultCommandeDetailsShouldNotBeFound("prix.equals=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByPrixIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where prix not equals to DEFAULT_PRIX
        defaultCommandeDetailsShouldNotBeFound("prix.notEquals=" + DEFAULT_PRIX);

        // Get all the commandeDetailsList where prix not equals to UPDATED_PRIX
        defaultCommandeDetailsShouldBeFound("prix.notEquals=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByPrixIsInShouldWork() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where prix in DEFAULT_PRIX or UPDATED_PRIX
        defaultCommandeDetailsShouldBeFound("prix.in=" + DEFAULT_PRIX + "," + UPDATED_PRIX);

        // Get all the commandeDetailsList where prix equals to UPDATED_PRIX
        defaultCommandeDetailsShouldNotBeFound("prix.in=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByPrixIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where prix is not null
        defaultCommandeDetailsShouldBeFound("prix.specified=true");

        // Get all the commandeDetailsList where prix is null
        defaultCommandeDetailsShouldNotBeFound("prix.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByPrixIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where prix is greater than or equal to DEFAULT_PRIX
        defaultCommandeDetailsShouldBeFound("prix.greaterThanOrEqual=" + DEFAULT_PRIX);

        // Get all the commandeDetailsList where prix is greater than or equal to UPDATED_PRIX
        defaultCommandeDetailsShouldNotBeFound("prix.greaterThanOrEqual=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByPrixIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where prix is less than or equal to DEFAULT_PRIX
        defaultCommandeDetailsShouldBeFound("prix.lessThanOrEqual=" + DEFAULT_PRIX);

        // Get all the commandeDetailsList where prix is less than or equal to SMALLER_PRIX
        defaultCommandeDetailsShouldNotBeFound("prix.lessThanOrEqual=" + SMALLER_PRIX);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByPrixIsLessThanSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where prix is less than DEFAULT_PRIX
        defaultCommandeDetailsShouldNotBeFound("prix.lessThan=" + DEFAULT_PRIX);

        // Get all the commandeDetailsList where prix is less than UPDATED_PRIX
        defaultCommandeDetailsShouldBeFound("prix.lessThan=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByPrixIsGreaterThanSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where prix is greater than DEFAULT_PRIX
        defaultCommandeDetailsShouldNotBeFound("prix.greaterThan=" + DEFAULT_PRIX);

        // Get all the commandeDetailsList where prix is greater than SMALLER_PRIX
        defaultCommandeDetailsShouldBeFound("prix.greaterThan=" + SMALLER_PRIX);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByEtatIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where etat equals to DEFAULT_ETAT
        defaultCommandeDetailsShouldBeFound("etat.equals=" + DEFAULT_ETAT);

        // Get all the commandeDetailsList where etat equals to UPDATED_ETAT
        defaultCommandeDetailsShouldNotBeFound("etat.equals=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByEtatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where etat not equals to DEFAULT_ETAT
        defaultCommandeDetailsShouldNotBeFound("etat.notEquals=" + DEFAULT_ETAT);

        // Get all the commandeDetailsList where etat not equals to UPDATED_ETAT
        defaultCommandeDetailsShouldBeFound("etat.notEquals=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByEtatIsInShouldWork() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where etat in DEFAULT_ETAT or UPDATED_ETAT
        defaultCommandeDetailsShouldBeFound("etat.in=" + DEFAULT_ETAT + "," + UPDATED_ETAT);

        // Get all the commandeDetailsList where etat equals to UPDATED_ETAT
        defaultCommandeDetailsShouldNotBeFound("etat.in=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByEtatIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where etat is not null
        defaultCommandeDetailsShouldBeFound("etat.specified=true");

        // Get all the commandeDetailsList where etat is null
        defaultCommandeDetailsShouldNotBeFound("etat.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByEtatContainsSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where etat contains DEFAULT_ETAT
        defaultCommandeDetailsShouldBeFound("etat.contains=" + DEFAULT_ETAT);

        // Get all the commandeDetailsList where etat contains UPDATED_ETAT
        defaultCommandeDetailsShouldNotBeFound("etat.contains=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByEtatNotContainsSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where etat does not contain DEFAULT_ETAT
        defaultCommandeDetailsShouldNotBeFound("etat.doesNotContain=" + DEFAULT_ETAT);

        // Get all the commandeDetailsList where etat does not contain UPDATED_ETAT
        defaultCommandeDetailsShouldBeFound("etat.doesNotContain=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByQteIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where qte equals to DEFAULT_QTE
        defaultCommandeDetailsShouldBeFound("qte.equals=" + DEFAULT_QTE);

        // Get all the commandeDetailsList where qte equals to UPDATED_QTE
        defaultCommandeDetailsShouldNotBeFound("qte.equals=" + UPDATED_QTE);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByQteIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where qte not equals to DEFAULT_QTE
        defaultCommandeDetailsShouldNotBeFound("qte.notEquals=" + DEFAULT_QTE);

        // Get all the commandeDetailsList where qte not equals to UPDATED_QTE
        defaultCommandeDetailsShouldBeFound("qte.notEquals=" + UPDATED_QTE);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByQteIsInShouldWork() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where qte in DEFAULT_QTE or UPDATED_QTE
        defaultCommandeDetailsShouldBeFound("qte.in=" + DEFAULT_QTE + "," + UPDATED_QTE);

        // Get all the commandeDetailsList where qte equals to UPDATED_QTE
        defaultCommandeDetailsShouldNotBeFound("qte.in=" + UPDATED_QTE);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByQteIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where qte is not null
        defaultCommandeDetailsShouldBeFound("qte.specified=true");

        // Get all the commandeDetailsList where qte is null
        defaultCommandeDetailsShouldNotBeFound("qte.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByQteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where qte is greater than or equal to DEFAULT_QTE
        defaultCommandeDetailsShouldBeFound("qte.greaterThanOrEqual=" + DEFAULT_QTE);

        // Get all the commandeDetailsList where qte is greater than or equal to UPDATED_QTE
        defaultCommandeDetailsShouldNotBeFound("qte.greaterThanOrEqual=" + UPDATED_QTE);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByQteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where qte is less than or equal to DEFAULT_QTE
        defaultCommandeDetailsShouldBeFound("qte.lessThanOrEqual=" + DEFAULT_QTE);

        // Get all the commandeDetailsList where qte is less than or equal to SMALLER_QTE
        defaultCommandeDetailsShouldNotBeFound("qte.lessThanOrEqual=" + SMALLER_QTE);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByQteIsLessThanSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where qte is less than DEFAULT_QTE
        defaultCommandeDetailsShouldNotBeFound("qte.lessThan=" + DEFAULT_QTE);

        // Get all the commandeDetailsList where qte is less than UPDATED_QTE
        defaultCommandeDetailsShouldBeFound("qte.lessThan=" + UPDATED_QTE);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByQteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        // Get all the commandeDetailsList where qte is greater than DEFAULT_QTE
        defaultCommandeDetailsShouldNotBeFound("qte.greaterThan=" + DEFAULT_QTE);

        // Get all the commandeDetailsList where qte is greater than SMALLER_QTE
        defaultCommandeDetailsShouldBeFound("qte.greaterThan=" + SMALLER_QTE);
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByCommandeIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);
        Commande commande;
        if (TestUtil.findAll(em, Commande.class).isEmpty()) {
            commande = CommandeResourceIT.createEntity(em);
            em.persist(commande);
            em.flush();
        } else {
            commande = TestUtil.findAll(em, Commande.class).get(0);
        }
        em.persist(commande);
        em.flush();
        commandeDetails.setCommande(commande);
        commandeDetailsRepository.saveAndFlush(commandeDetails);
        Long commandeId = commande.getId();

        // Get all the commandeDetailsList where commande equals to commandeId
        defaultCommandeDetailsShouldBeFound("commandeId.equals=" + commandeId);

        // Get all the commandeDetailsList where commande equals to (commandeId + 1)
        defaultCommandeDetailsShouldNotBeFound("commandeId.equals=" + (commandeId + 1));
    }

    @Test
    @Transactional
    void getAllCommandeDetailsByPlatIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);
        Plat plat;
        if (TestUtil.findAll(em, Plat.class).isEmpty()) {
            plat = PlatResourceIT.createEntity(em);
            em.persist(plat);
            em.flush();
        } else {
            plat = TestUtil.findAll(em, Plat.class).get(0);
        }
        em.persist(plat);
        em.flush();
        commandeDetails.setPlat(plat);
        commandeDetailsRepository.saveAndFlush(commandeDetails);
        Long platId = plat.getId();

        // Get all the commandeDetailsList where plat equals to platId
        defaultCommandeDetailsShouldBeFound("platId.equals=" + platId);

        // Get all the commandeDetailsList where plat equals to (platId + 1)
        defaultCommandeDetailsShouldNotBeFound("platId.equals=" + (platId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommandeDetailsShouldBeFound(String filter) throws Exception {
        restCommandeDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commandeDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(DEFAULT_PRIX.doubleValue())))
            .andExpect(jsonPath("$.[*].etat").value(hasItem(DEFAULT_ETAT)))
            .andExpect(jsonPath("$.[*].qte").value(hasItem(DEFAULT_QTE.doubleValue())));

        // Check, that the count call also returns 1
        restCommandeDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommandeDetailsShouldNotBeFound(String filter) throws Exception {
        restCommandeDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommandeDetailsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCommandeDetails() throws Exception {
        // Get the commandeDetails
        restCommandeDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCommandeDetails() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        int databaseSizeBeforeUpdate = commandeDetailsRepository.findAll().size();

        // Update the commandeDetails
        CommandeDetails updatedCommandeDetails = commandeDetailsRepository.findById(commandeDetails.getId()).get();
        // Disconnect from session so that the updates on updatedCommandeDetails are not directly saved in db
        em.detach(updatedCommandeDetails);
        updatedCommandeDetails.prix(UPDATED_PRIX).etat(UPDATED_ETAT).qte(UPDATED_QTE);
        CommandeDetailsDTO commandeDetailsDTO = commandeDetailsMapper.toDto(updatedCommandeDetails);

        restCommandeDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandeDetailsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDetailsDTO))
            )
            .andExpect(status().isOk());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeUpdate);
        CommandeDetails testCommandeDetails = commandeDetailsList.get(commandeDetailsList.size() - 1);
        assertThat(testCommandeDetails.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testCommandeDetails.getEtat()).isEqualTo(UPDATED_ETAT);
        assertThat(testCommandeDetails.getQte()).isEqualTo(UPDATED_QTE);
    }

    @Test
    @Transactional
    void putNonExistingCommandeDetails() throws Exception {
        int databaseSizeBeforeUpdate = commandeDetailsRepository.findAll().size();
        commandeDetails.setId(count.incrementAndGet());

        // Create the CommandeDetails
        CommandeDetailsDTO commandeDetailsDTO = commandeDetailsMapper.toDto(commandeDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandeDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandeDetailsDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommandeDetails() throws Exception {
        int databaseSizeBeforeUpdate = commandeDetailsRepository.findAll().size();
        commandeDetails.setId(count.incrementAndGet());

        // Create the CommandeDetails
        CommandeDetailsDTO commandeDetailsDTO = commandeDetailsMapper.toDto(commandeDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommandeDetails() throws Exception {
        int databaseSizeBeforeUpdate = commandeDetailsRepository.findAll().size();
        commandeDetails.setId(count.incrementAndGet());

        // Create the CommandeDetails
        CommandeDetailsDTO commandeDetailsDTO = commandeDetailsMapper.toDto(commandeDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeDetailsMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDetailsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommandeDetailsWithPatch() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        int databaseSizeBeforeUpdate = commandeDetailsRepository.findAll().size();

        // Update the commandeDetails using partial update
        CommandeDetails partialUpdatedCommandeDetails = new CommandeDetails();
        partialUpdatedCommandeDetails.setId(commandeDetails.getId());

        partialUpdatedCommandeDetails.prix(UPDATED_PRIX).etat(UPDATED_ETAT).qte(UPDATED_QTE);

        restCommandeDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommandeDetails.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommandeDetails))
            )
            .andExpect(status().isOk());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeUpdate);
        CommandeDetails testCommandeDetails = commandeDetailsList.get(commandeDetailsList.size() - 1);
        assertThat(testCommandeDetails.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testCommandeDetails.getEtat()).isEqualTo(UPDATED_ETAT);
        assertThat(testCommandeDetails.getQte()).isEqualTo(UPDATED_QTE);
    }

    @Test
    @Transactional
    void fullUpdateCommandeDetailsWithPatch() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        int databaseSizeBeforeUpdate = commandeDetailsRepository.findAll().size();

        // Update the commandeDetails using partial update
        CommandeDetails partialUpdatedCommandeDetails = new CommandeDetails();
        partialUpdatedCommandeDetails.setId(commandeDetails.getId());

        partialUpdatedCommandeDetails.prix(UPDATED_PRIX).etat(UPDATED_ETAT).qte(UPDATED_QTE);

        restCommandeDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommandeDetails.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommandeDetails))
            )
            .andExpect(status().isOk());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeUpdate);
        CommandeDetails testCommandeDetails = commandeDetailsList.get(commandeDetailsList.size() - 1);
        assertThat(testCommandeDetails.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testCommandeDetails.getEtat()).isEqualTo(UPDATED_ETAT);
        assertThat(testCommandeDetails.getQte()).isEqualTo(UPDATED_QTE);
    }

    @Test
    @Transactional
    void patchNonExistingCommandeDetails() throws Exception {
        int databaseSizeBeforeUpdate = commandeDetailsRepository.findAll().size();
        commandeDetails.setId(count.incrementAndGet());

        // Create the CommandeDetails
        CommandeDetailsDTO commandeDetailsDTO = commandeDetailsMapper.toDto(commandeDetails);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandeDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, commandeDetailsDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandeDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommandeDetails() throws Exception {
        int databaseSizeBeforeUpdate = commandeDetailsRepository.findAll().size();
        commandeDetails.setId(count.incrementAndGet());

        // Create the CommandeDetails
        CommandeDetailsDTO commandeDetailsDTO = commandeDetailsMapper.toDto(commandeDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandeDetailsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommandeDetails() throws Exception {
        int databaseSizeBeforeUpdate = commandeDetailsRepository.findAll().size();
        commandeDetails.setId(count.incrementAndGet());

        // Create the CommandeDetails
        CommandeDetailsDTO commandeDetailsDTO = commandeDetailsMapper.toDto(commandeDetails);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandeDetailsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CommandeDetails in the database
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommandeDetails() throws Exception {
        // Initialize the database
        commandeDetailsRepository.saveAndFlush(commandeDetails);

        int databaseSizeBeforeDelete = commandeDetailsRepository.findAll().size();

        // Delete the commandeDetails
        restCommandeDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, commandeDetails.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CommandeDetails> commandeDetailsList = commandeDetailsRepository.findAll();
        assertThat(commandeDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
