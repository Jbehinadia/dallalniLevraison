package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Livreur;
import com.mycompany.myapp.repository.LivreurRepository;
import com.mycompany.myapp.service.criteria.LivreurCriteria;
import com.mycompany.myapp.service.dto.LivreurDTO;
import com.mycompany.myapp.service.mapper.LivreurMapper;
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
 * Integration tests for the {@link LivreurResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LivreurResourceIT {

    private static final String DEFAULT_NOM_LIVREUR = "AAAAAAAAAA";
    private static final String UPDATED_NOM_LIVREUR = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM_LIVREUR = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM_LIVREUR = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE_LIVREUR = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE_LIVREUR = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_LIVREUR = "AAAAAAAAAA";
    private static final String UPDATED_NUM_LIVREUR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/livreurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LivreurRepository livreurRepository;

    @Autowired
    private LivreurMapper livreurMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLivreurMockMvc;

    private Livreur livreur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livreur createEntity(EntityManager em) {
        Livreur livreur = new Livreur()
            .nomLivreur(DEFAULT_NOM_LIVREUR)
            .prenomLivreur(DEFAULT_PRENOM_LIVREUR)
            .adresseLivreur(DEFAULT_ADRESSE_LIVREUR)
            .numLivreur(DEFAULT_NUM_LIVREUR);
        return livreur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livreur createUpdatedEntity(EntityManager em) {
        Livreur livreur = new Livreur()
            .nomLivreur(UPDATED_NOM_LIVREUR)
            .prenomLivreur(UPDATED_PRENOM_LIVREUR)
            .adresseLivreur(UPDATED_ADRESSE_LIVREUR)
            .numLivreur(UPDATED_NUM_LIVREUR);
        return livreur;
    }

    @BeforeEach
    public void initTest() {
        livreur = createEntity(em);
    }

    @Test
    @Transactional
    void createLivreur() throws Exception {
        int databaseSizeBeforeCreate = livreurRepository.findAll().size();
        // Create the Livreur
        LivreurDTO livreurDTO = livreurMapper.toDto(livreur);
        restLivreurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livreurDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeCreate + 1);
        Livreur testLivreur = livreurList.get(livreurList.size() - 1);
        assertThat(testLivreur.getNomLivreur()).isEqualTo(DEFAULT_NOM_LIVREUR);
        assertThat(testLivreur.getPrenomLivreur()).isEqualTo(DEFAULT_PRENOM_LIVREUR);
        assertThat(testLivreur.getAdresseLivreur()).isEqualTo(DEFAULT_ADRESSE_LIVREUR);
        assertThat(testLivreur.getNumLivreur()).isEqualTo(DEFAULT_NUM_LIVREUR);
    }

    @Test
    @Transactional
    void createLivreurWithExistingId() throws Exception {
        // Create the Livreur with an existing ID
        livreur.setId(1L);
        LivreurDTO livreurDTO = livreurMapper.toDto(livreur);

        int databaseSizeBeforeCreate = livreurRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLivreurMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livreurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLivreurs() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList
        restLivreurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(livreur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomLivreur").value(hasItem(DEFAULT_NOM_LIVREUR)))
            .andExpect(jsonPath("$.[*].prenomLivreur").value(hasItem(DEFAULT_PRENOM_LIVREUR)))
            .andExpect(jsonPath("$.[*].adresseLivreur").value(hasItem(DEFAULT_ADRESSE_LIVREUR)))
            .andExpect(jsonPath("$.[*].numLivreur").value(hasItem(DEFAULT_NUM_LIVREUR)));
    }

    @Test
    @Transactional
    void getLivreur() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get the livreur
        restLivreurMockMvc
            .perform(get(ENTITY_API_URL_ID, livreur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(livreur.getId().intValue()))
            .andExpect(jsonPath("$.nomLivreur").value(DEFAULT_NOM_LIVREUR))
            .andExpect(jsonPath("$.prenomLivreur").value(DEFAULT_PRENOM_LIVREUR))
            .andExpect(jsonPath("$.adresseLivreur").value(DEFAULT_ADRESSE_LIVREUR))
            .andExpect(jsonPath("$.numLivreur").value(DEFAULT_NUM_LIVREUR));
    }

    @Test
    @Transactional
    void getLivreursByIdFiltering() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        Long id = livreur.getId();

        defaultLivreurShouldBeFound("id.equals=" + id);
        defaultLivreurShouldNotBeFound("id.notEquals=" + id);

        defaultLivreurShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultLivreurShouldNotBeFound("id.greaterThan=" + id);

        defaultLivreurShouldBeFound("id.lessThanOrEqual=" + id);
        defaultLivreurShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLivreursByNomLivreurIsEqualToSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where nomLivreur equals to DEFAULT_NOM_LIVREUR
        defaultLivreurShouldBeFound("nomLivreur.equals=" + DEFAULT_NOM_LIVREUR);

        // Get all the livreurList where nomLivreur equals to UPDATED_NOM_LIVREUR
        defaultLivreurShouldNotBeFound("nomLivreur.equals=" + UPDATED_NOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByNomLivreurIsNotEqualToSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where nomLivreur not equals to DEFAULT_NOM_LIVREUR
        defaultLivreurShouldNotBeFound("nomLivreur.notEquals=" + DEFAULT_NOM_LIVREUR);

        // Get all the livreurList where nomLivreur not equals to UPDATED_NOM_LIVREUR
        defaultLivreurShouldBeFound("nomLivreur.notEquals=" + UPDATED_NOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByNomLivreurIsInShouldWork() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where nomLivreur in DEFAULT_NOM_LIVREUR or UPDATED_NOM_LIVREUR
        defaultLivreurShouldBeFound("nomLivreur.in=" + DEFAULT_NOM_LIVREUR + "," + UPDATED_NOM_LIVREUR);

        // Get all the livreurList where nomLivreur equals to UPDATED_NOM_LIVREUR
        defaultLivreurShouldNotBeFound("nomLivreur.in=" + UPDATED_NOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByNomLivreurIsNullOrNotNull() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where nomLivreur is not null
        defaultLivreurShouldBeFound("nomLivreur.specified=true");

        // Get all the livreurList where nomLivreur is null
        defaultLivreurShouldNotBeFound("nomLivreur.specified=false");
    }

    @Test
    @Transactional
    void getAllLivreursByNomLivreurContainsSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where nomLivreur contains DEFAULT_NOM_LIVREUR
        defaultLivreurShouldBeFound("nomLivreur.contains=" + DEFAULT_NOM_LIVREUR);

        // Get all the livreurList where nomLivreur contains UPDATED_NOM_LIVREUR
        defaultLivreurShouldNotBeFound("nomLivreur.contains=" + UPDATED_NOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByNomLivreurNotContainsSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where nomLivreur does not contain DEFAULT_NOM_LIVREUR
        defaultLivreurShouldNotBeFound("nomLivreur.doesNotContain=" + DEFAULT_NOM_LIVREUR);

        // Get all the livreurList where nomLivreur does not contain UPDATED_NOM_LIVREUR
        defaultLivreurShouldBeFound("nomLivreur.doesNotContain=" + UPDATED_NOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByPrenomLivreurIsEqualToSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where prenomLivreur equals to DEFAULT_PRENOM_LIVREUR
        defaultLivreurShouldBeFound("prenomLivreur.equals=" + DEFAULT_PRENOM_LIVREUR);

        // Get all the livreurList where prenomLivreur equals to UPDATED_PRENOM_LIVREUR
        defaultLivreurShouldNotBeFound("prenomLivreur.equals=" + UPDATED_PRENOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByPrenomLivreurIsNotEqualToSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where prenomLivreur not equals to DEFAULT_PRENOM_LIVREUR
        defaultLivreurShouldNotBeFound("prenomLivreur.notEquals=" + DEFAULT_PRENOM_LIVREUR);

        // Get all the livreurList where prenomLivreur not equals to UPDATED_PRENOM_LIVREUR
        defaultLivreurShouldBeFound("prenomLivreur.notEquals=" + UPDATED_PRENOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByPrenomLivreurIsInShouldWork() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where prenomLivreur in DEFAULT_PRENOM_LIVREUR or UPDATED_PRENOM_LIVREUR
        defaultLivreurShouldBeFound("prenomLivreur.in=" + DEFAULT_PRENOM_LIVREUR + "," + UPDATED_PRENOM_LIVREUR);

        // Get all the livreurList where prenomLivreur equals to UPDATED_PRENOM_LIVREUR
        defaultLivreurShouldNotBeFound("prenomLivreur.in=" + UPDATED_PRENOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByPrenomLivreurIsNullOrNotNull() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where prenomLivreur is not null
        defaultLivreurShouldBeFound("prenomLivreur.specified=true");

        // Get all the livreurList where prenomLivreur is null
        defaultLivreurShouldNotBeFound("prenomLivreur.specified=false");
    }

    @Test
    @Transactional
    void getAllLivreursByPrenomLivreurContainsSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where prenomLivreur contains DEFAULT_PRENOM_LIVREUR
        defaultLivreurShouldBeFound("prenomLivreur.contains=" + DEFAULT_PRENOM_LIVREUR);

        // Get all the livreurList where prenomLivreur contains UPDATED_PRENOM_LIVREUR
        defaultLivreurShouldNotBeFound("prenomLivreur.contains=" + UPDATED_PRENOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByPrenomLivreurNotContainsSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where prenomLivreur does not contain DEFAULT_PRENOM_LIVREUR
        defaultLivreurShouldNotBeFound("prenomLivreur.doesNotContain=" + DEFAULT_PRENOM_LIVREUR);

        // Get all the livreurList where prenomLivreur does not contain UPDATED_PRENOM_LIVREUR
        defaultLivreurShouldBeFound("prenomLivreur.doesNotContain=" + UPDATED_PRENOM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByAdresseLivreurIsEqualToSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where adresseLivreur equals to DEFAULT_ADRESSE_LIVREUR
        defaultLivreurShouldBeFound("adresseLivreur.equals=" + DEFAULT_ADRESSE_LIVREUR);

        // Get all the livreurList where adresseLivreur equals to UPDATED_ADRESSE_LIVREUR
        defaultLivreurShouldNotBeFound("adresseLivreur.equals=" + UPDATED_ADRESSE_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByAdresseLivreurIsNotEqualToSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where adresseLivreur not equals to DEFAULT_ADRESSE_LIVREUR
        defaultLivreurShouldNotBeFound("adresseLivreur.notEquals=" + DEFAULT_ADRESSE_LIVREUR);

        // Get all the livreurList where adresseLivreur not equals to UPDATED_ADRESSE_LIVREUR
        defaultLivreurShouldBeFound("adresseLivreur.notEquals=" + UPDATED_ADRESSE_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByAdresseLivreurIsInShouldWork() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where adresseLivreur in DEFAULT_ADRESSE_LIVREUR or UPDATED_ADRESSE_LIVREUR
        defaultLivreurShouldBeFound("adresseLivreur.in=" + DEFAULT_ADRESSE_LIVREUR + "," + UPDATED_ADRESSE_LIVREUR);

        // Get all the livreurList where adresseLivreur equals to UPDATED_ADRESSE_LIVREUR
        defaultLivreurShouldNotBeFound("adresseLivreur.in=" + UPDATED_ADRESSE_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByAdresseLivreurIsNullOrNotNull() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where adresseLivreur is not null
        defaultLivreurShouldBeFound("adresseLivreur.specified=true");

        // Get all the livreurList where adresseLivreur is null
        defaultLivreurShouldNotBeFound("adresseLivreur.specified=false");
    }

    @Test
    @Transactional
    void getAllLivreursByAdresseLivreurContainsSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where adresseLivreur contains DEFAULT_ADRESSE_LIVREUR
        defaultLivreurShouldBeFound("adresseLivreur.contains=" + DEFAULT_ADRESSE_LIVREUR);

        // Get all the livreurList where adresseLivreur contains UPDATED_ADRESSE_LIVREUR
        defaultLivreurShouldNotBeFound("adresseLivreur.contains=" + UPDATED_ADRESSE_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByAdresseLivreurNotContainsSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where adresseLivreur does not contain DEFAULT_ADRESSE_LIVREUR
        defaultLivreurShouldNotBeFound("adresseLivreur.doesNotContain=" + DEFAULT_ADRESSE_LIVREUR);

        // Get all the livreurList where adresseLivreur does not contain UPDATED_ADRESSE_LIVREUR
        defaultLivreurShouldBeFound("adresseLivreur.doesNotContain=" + UPDATED_ADRESSE_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByNumLivreurIsEqualToSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where numLivreur equals to DEFAULT_NUM_LIVREUR
        defaultLivreurShouldBeFound("numLivreur.equals=" + DEFAULT_NUM_LIVREUR);

        // Get all the livreurList where numLivreur equals to UPDATED_NUM_LIVREUR
        defaultLivreurShouldNotBeFound("numLivreur.equals=" + UPDATED_NUM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByNumLivreurIsNotEqualToSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where numLivreur not equals to DEFAULT_NUM_LIVREUR
        defaultLivreurShouldNotBeFound("numLivreur.notEquals=" + DEFAULT_NUM_LIVREUR);

        // Get all the livreurList where numLivreur not equals to UPDATED_NUM_LIVREUR
        defaultLivreurShouldBeFound("numLivreur.notEquals=" + UPDATED_NUM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByNumLivreurIsInShouldWork() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where numLivreur in DEFAULT_NUM_LIVREUR or UPDATED_NUM_LIVREUR
        defaultLivreurShouldBeFound("numLivreur.in=" + DEFAULT_NUM_LIVREUR + "," + UPDATED_NUM_LIVREUR);

        // Get all the livreurList where numLivreur equals to UPDATED_NUM_LIVREUR
        defaultLivreurShouldNotBeFound("numLivreur.in=" + UPDATED_NUM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByNumLivreurIsNullOrNotNull() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where numLivreur is not null
        defaultLivreurShouldBeFound("numLivreur.specified=true");

        // Get all the livreurList where numLivreur is null
        defaultLivreurShouldNotBeFound("numLivreur.specified=false");
    }

    @Test
    @Transactional
    void getAllLivreursByNumLivreurContainsSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where numLivreur contains DEFAULT_NUM_LIVREUR
        defaultLivreurShouldBeFound("numLivreur.contains=" + DEFAULT_NUM_LIVREUR);

        // Get all the livreurList where numLivreur contains UPDATED_NUM_LIVREUR
        defaultLivreurShouldNotBeFound("numLivreur.contains=" + UPDATED_NUM_LIVREUR);
    }

    @Test
    @Transactional
    void getAllLivreursByNumLivreurNotContainsSomething() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        // Get all the livreurList where numLivreur does not contain DEFAULT_NUM_LIVREUR
        defaultLivreurShouldNotBeFound("numLivreur.doesNotContain=" + DEFAULT_NUM_LIVREUR);

        // Get all the livreurList where numLivreur does not contain UPDATED_NUM_LIVREUR
        defaultLivreurShouldBeFound("numLivreur.doesNotContain=" + UPDATED_NUM_LIVREUR);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLivreurShouldBeFound(String filter) throws Exception {
        restLivreurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(livreur.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomLivreur").value(hasItem(DEFAULT_NOM_LIVREUR)))
            .andExpect(jsonPath("$.[*].prenomLivreur").value(hasItem(DEFAULT_PRENOM_LIVREUR)))
            .andExpect(jsonPath("$.[*].adresseLivreur").value(hasItem(DEFAULT_ADRESSE_LIVREUR)))
            .andExpect(jsonPath("$.[*].numLivreur").value(hasItem(DEFAULT_NUM_LIVREUR)));

        // Check, that the count call also returns 1
        restLivreurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLivreurShouldNotBeFound(String filter) throws Exception {
        restLivreurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLivreurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLivreur() throws Exception {
        // Get the livreur
        restLivreurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewLivreur() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        int databaseSizeBeforeUpdate = livreurRepository.findAll().size();

        // Update the livreur
        Livreur updatedLivreur = livreurRepository.findById(livreur.getId()).get();
        // Disconnect from session so that the updates on updatedLivreur are not directly saved in db
        em.detach(updatedLivreur);
        updatedLivreur
            .nomLivreur(UPDATED_NOM_LIVREUR)
            .prenomLivreur(UPDATED_PRENOM_LIVREUR)
            .adresseLivreur(UPDATED_ADRESSE_LIVREUR)
            .numLivreur(UPDATED_NUM_LIVREUR);
        LivreurDTO livreurDTO = livreurMapper.toDto(updatedLivreur);

        restLivreurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, livreurDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livreurDTO))
            )
            .andExpect(status().isOk());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
        Livreur testLivreur = livreurList.get(livreurList.size() - 1);
        assertThat(testLivreur.getNomLivreur()).isEqualTo(UPDATED_NOM_LIVREUR);
        assertThat(testLivreur.getPrenomLivreur()).isEqualTo(UPDATED_PRENOM_LIVREUR);
        assertThat(testLivreur.getAdresseLivreur()).isEqualTo(UPDATED_ADRESSE_LIVREUR);
        assertThat(testLivreur.getNumLivreur()).isEqualTo(UPDATED_NUM_LIVREUR);
    }

    @Test
    @Transactional
    void putNonExistingLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().size();
        livreur.setId(count.incrementAndGet());

        // Create the Livreur
        LivreurDTO livreurDTO = livreurMapper.toDto(livreur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivreurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, livreurDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livreurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().size();
        livreur.setId(count.incrementAndGet());

        // Create the Livreur
        LivreurDTO livreurDTO = livreurMapper.toDto(livreur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livreurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().size();
        livreur.setId(count.incrementAndGet());

        // Create the Livreur
        LivreurDTO livreurDTO = livreurMapper.toDto(livreur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreurMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(livreurDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLivreurWithPatch() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        int databaseSizeBeforeUpdate = livreurRepository.findAll().size();

        // Update the livreur using partial update
        Livreur partialUpdatedLivreur = new Livreur();
        partialUpdatedLivreur.setId(livreur.getId());

        partialUpdatedLivreur.nomLivreur(UPDATED_NOM_LIVREUR);

        restLivreurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivreur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLivreur))
            )
            .andExpect(status().isOk());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
        Livreur testLivreur = livreurList.get(livreurList.size() - 1);
        assertThat(testLivreur.getNomLivreur()).isEqualTo(UPDATED_NOM_LIVREUR);
        assertThat(testLivreur.getPrenomLivreur()).isEqualTo(DEFAULT_PRENOM_LIVREUR);
        assertThat(testLivreur.getAdresseLivreur()).isEqualTo(DEFAULT_ADRESSE_LIVREUR);
        assertThat(testLivreur.getNumLivreur()).isEqualTo(DEFAULT_NUM_LIVREUR);
    }

    @Test
    @Transactional
    void fullUpdateLivreurWithPatch() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        int databaseSizeBeforeUpdate = livreurRepository.findAll().size();

        // Update the livreur using partial update
        Livreur partialUpdatedLivreur = new Livreur();
        partialUpdatedLivreur.setId(livreur.getId());

        partialUpdatedLivreur
            .nomLivreur(UPDATED_NOM_LIVREUR)
            .prenomLivreur(UPDATED_PRENOM_LIVREUR)
            .adresseLivreur(UPDATED_ADRESSE_LIVREUR)
            .numLivreur(UPDATED_NUM_LIVREUR);

        restLivreurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLivreur.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedLivreur))
            )
            .andExpect(status().isOk());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
        Livreur testLivreur = livreurList.get(livreurList.size() - 1);
        assertThat(testLivreur.getNomLivreur()).isEqualTo(UPDATED_NOM_LIVREUR);
        assertThat(testLivreur.getPrenomLivreur()).isEqualTo(UPDATED_PRENOM_LIVREUR);
        assertThat(testLivreur.getAdresseLivreur()).isEqualTo(UPDATED_ADRESSE_LIVREUR);
        assertThat(testLivreur.getNumLivreur()).isEqualTo(UPDATED_NUM_LIVREUR);
    }

    @Test
    @Transactional
    void patchNonExistingLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().size();
        livreur.setId(count.incrementAndGet());

        // Create the Livreur
        LivreurDTO livreurDTO = livreurMapper.toDto(livreur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLivreurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, livreurDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(livreurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().size();
        livreur.setId(count.incrementAndGet());

        // Create the Livreur
        LivreurDTO livreurDTO = livreurMapper.toDto(livreur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(livreurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLivreur() throws Exception {
        int databaseSizeBeforeUpdate = livreurRepository.findAll().size();
        livreur.setId(count.incrementAndGet());

        // Create the Livreur
        LivreurDTO livreurDTO = livreurMapper.toDto(livreur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLivreurMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(livreurDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Livreur in the database
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLivreur() throws Exception {
        // Initialize the database
        livreurRepository.saveAndFlush(livreur);

        int databaseSizeBeforeDelete = livreurRepository.findAll().size();

        // Delete the livreur
        restLivreurMockMvc
            .perform(delete(ENTITY_API_URL_ID, livreur.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Livreur> livreurList = livreurRepository.findAll();
        assertThat(livreurList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
