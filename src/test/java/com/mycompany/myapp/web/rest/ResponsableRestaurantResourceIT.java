package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ResponsableRestaurant;
import com.mycompany.myapp.repository.ResponsableRestaurantRepository;
import com.mycompany.myapp.service.dto.ResponsableRestaurantDTO;
import com.mycompany.myapp.service.mapper.ResponsableRestaurantMapper;
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
 * Integration tests for the {@link ResponsableRestaurantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ResponsableRestaurantResourceIT {

    private static final String DEFAULT_NOM_RESPONSABLE = "AAAAAAAAAA";
    private static final String UPDATED_NOM_RESPONSABLE = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM_RESPONSABLE = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM_RESPONSABLE = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE_RESPONSABLE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE_RESPONSABLE = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_RESPONSABLE = "AAAAAAAAAA";
    private static final String UPDATED_NUM_RESPONSABLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/responsable-restaurants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ResponsableRestaurantRepository responsableRestaurantRepository;

    @Autowired
    private ResponsableRestaurantMapper responsableRestaurantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restResponsableRestaurantMockMvc;

    private ResponsableRestaurant responsableRestaurant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResponsableRestaurant createEntity(EntityManager em) {
        ResponsableRestaurant responsableRestaurant = new ResponsableRestaurant()
            .nomResponsable(DEFAULT_NOM_RESPONSABLE)
            .prenomResponsable(DEFAULT_PRENOM_RESPONSABLE)
            .adresseResponsable(DEFAULT_ADRESSE_RESPONSABLE)
            .numResponsable(DEFAULT_NUM_RESPONSABLE);
        return responsableRestaurant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ResponsableRestaurant createUpdatedEntity(EntityManager em) {
        ResponsableRestaurant responsableRestaurant = new ResponsableRestaurant()
            .nomResponsable(UPDATED_NOM_RESPONSABLE)
            .prenomResponsable(UPDATED_PRENOM_RESPONSABLE)
            .adresseResponsable(UPDATED_ADRESSE_RESPONSABLE)
            .numResponsable(UPDATED_NUM_RESPONSABLE);
        return responsableRestaurant;
    }

    @BeforeEach
    public void initTest() {
        responsableRestaurant = createEntity(em);
    }

    @Test
    @Transactional
    void createResponsableRestaurant() throws Exception {
        int databaseSizeBeforeCreate = responsableRestaurantRepository.findAll().size();
        // Create the ResponsableRestaurant
        ResponsableRestaurantDTO responsableRestaurantDTO = responsableRestaurantMapper.toDto(responsableRestaurant);
        restResponsableRestaurantMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(responsableRestaurantDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeCreate + 1);
        ResponsableRestaurant testResponsableRestaurant = responsableRestaurantList.get(responsableRestaurantList.size() - 1);
        assertThat(testResponsableRestaurant.getNomResponsable()).isEqualTo(DEFAULT_NOM_RESPONSABLE);
        assertThat(testResponsableRestaurant.getPrenomResponsable()).isEqualTo(DEFAULT_PRENOM_RESPONSABLE);
        assertThat(testResponsableRestaurant.getAdresseResponsable()).isEqualTo(DEFAULT_ADRESSE_RESPONSABLE);
        assertThat(testResponsableRestaurant.getNumResponsable()).isEqualTo(DEFAULT_NUM_RESPONSABLE);
    }

    @Test
    @Transactional
    void createResponsableRestaurantWithExistingId() throws Exception {
        // Create the ResponsableRestaurant with an existing ID
        responsableRestaurant.setId(1L);
        ResponsableRestaurantDTO responsableRestaurantDTO = responsableRestaurantMapper.toDto(responsableRestaurant);

        int databaseSizeBeforeCreate = responsableRestaurantRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restResponsableRestaurantMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(responsableRestaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurants() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList
        restResponsableRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(responsableRestaurant.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomResponsable").value(hasItem(DEFAULT_NOM_RESPONSABLE)))
            .andExpect(jsonPath("$.[*].prenomResponsable").value(hasItem(DEFAULT_PRENOM_RESPONSABLE)))
            .andExpect(jsonPath("$.[*].adresseResponsable").value(hasItem(DEFAULT_ADRESSE_RESPONSABLE)))
            .andExpect(jsonPath("$.[*].numResponsable").value(hasItem(DEFAULT_NUM_RESPONSABLE)));
    }

    @Test
    @Transactional
    void getResponsableRestaurant() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get the responsableRestaurant
        restResponsableRestaurantMockMvc
            .perform(get(ENTITY_API_URL_ID, responsableRestaurant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(responsableRestaurant.getId().intValue()))
            .andExpect(jsonPath("$.nomResponsable").value(DEFAULT_NOM_RESPONSABLE))
            .andExpect(jsonPath("$.prenomResponsable").value(DEFAULT_PRENOM_RESPONSABLE))
            .andExpect(jsonPath("$.adresseResponsable").value(DEFAULT_ADRESSE_RESPONSABLE))
            .andExpect(jsonPath("$.numResponsable").value(DEFAULT_NUM_RESPONSABLE));
    }

    @Test
    @Transactional
    void getResponsableRestaurantsByIdFiltering() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        Long id = responsableRestaurant.getId();

        defaultResponsableRestaurantShouldBeFound("id.equals=" + id);
        defaultResponsableRestaurantShouldNotBeFound("id.notEquals=" + id);

        defaultResponsableRestaurantShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultResponsableRestaurantShouldNotBeFound("id.greaterThan=" + id);

        defaultResponsableRestaurantShouldBeFound("id.lessThanOrEqual=" + id);
        defaultResponsableRestaurantShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNomResponsableIsEqualToSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where nomResponsable equals to DEFAULT_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("nomResponsable.equals=" + DEFAULT_NOM_RESPONSABLE);

        // Get all the responsableRestaurantList where nomResponsable equals to UPDATED_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("nomResponsable.equals=" + UPDATED_NOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNomResponsableIsNotEqualToSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where nomResponsable not equals to DEFAULT_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("nomResponsable.notEquals=" + DEFAULT_NOM_RESPONSABLE);

        // Get all the responsableRestaurantList where nomResponsable not equals to UPDATED_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("nomResponsable.notEquals=" + UPDATED_NOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNomResponsableIsInShouldWork() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where nomResponsable in DEFAULT_NOM_RESPONSABLE or UPDATED_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("nomResponsable.in=" + DEFAULT_NOM_RESPONSABLE + "," + UPDATED_NOM_RESPONSABLE);

        // Get all the responsableRestaurantList where nomResponsable equals to UPDATED_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("nomResponsable.in=" + UPDATED_NOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNomResponsableIsNullOrNotNull() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where nomResponsable is not null
        defaultResponsableRestaurantShouldBeFound("nomResponsable.specified=true");

        // Get all the responsableRestaurantList where nomResponsable is null
        defaultResponsableRestaurantShouldNotBeFound("nomResponsable.specified=false");
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNomResponsableContainsSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where nomResponsable contains DEFAULT_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("nomResponsable.contains=" + DEFAULT_NOM_RESPONSABLE);

        // Get all the responsableRestaurantList where nomResponsable contains UPDATED_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("nomResponsable.contains=" + UPDATED_NOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNomResponsableNotContainsSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where nomResponsable does not contain DEFAULT_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("nomResponsable.doesNotContain=" + DEFAULT_NOM_RESPONSABLE);

        // Get all the responsableRestaurantList where nomResponsable does not contain UPDATED_NOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("nomResponsable.doesNotContain=" + UPDATED_NOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByPrenomResponsableIsEqualToSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where prenomResponsable equals to DEFAULT_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("prenomResponsable.equals=" + DEFAULT_PRENOM_RESPONSABLE);

        // Get all the responsableRestaurantList where prenomResponsable equals to UPDATED_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("prenomResponsable.equals=" + UPDATED_PRENOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByPrenomResponsableIsNotEqualToSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where prenomResponsable not equals to DEFAULT_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("prenomResponsable.notEquals=" + DEFAULT_PRENOM_RESPONSABLE);

        // Get all the responsableRestaurantList where prenomResponsable not equals to UPDATED_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("prenomResponsable.notEquals=" + UPDATED_PRENOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByPrenomResponsableIsInShouldWork() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where prenomResponsable in DEFAULT_PRENOM_RESPONSABLE or UPDATED_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("prenomResponsable.in=" + DEFAULT_PRENOM_RESPONSABLE + "," + UPDATED_PRENOM_RESPONSABLE);

        // Get all the responsableRestaurantList where prenomResponsable equals to UPDATED_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("prenomResponsable.in=" + UPDATED_PRENOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByPrenomResponsableIsNullOrNotNull() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where prenomResponsable is not null
        defaultResponsableRestaurantShouldBeFound("prenomResponsable.specified=true");

        // Get all the responsableRestaurantList where prenomResponsable is null
        defaultResponsableRestaurantShouldNotBeFound("prenomResponsable.specified=false");
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByPrenomResponsableContainsSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where prenomResponsable contains DEFAULT_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("prenomResponsable.contains=" + DEFAULT_PRENOM_RESPONSABLE);

        // Get all the responsableRestaurantList where prenomResponsable contains UPDATED_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("prenomResponsable.contains=" + UPDATED_PRENOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByPrenomResponsableNotContainsSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where prenomResponsable does not contain DEFAULT_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("prenomResponsable.doesNotContain=" + DEFAULT_PRENOM_RESPONSABLE);

        // Get all the responsableRestaurantList where prenomResponsable does not contain UPDATED_PRENOM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("prenomResponsable.doesNotContain=" + UPDATED_PRENOM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByAdresseResponsableIsEqualToSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where adresseResponsable equals to DEFAULT_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("adresseResponsable.equals=" + DEFAULT_ADRESSE_RESPONSABLE);

        // Get all the responsableRestaurantList where adresseResponsable equals to UPDATED_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("adresseResponsable.equals=" + UPDATED_ADRESSE_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByAdresseResponsableIsNotEqualToSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where adresseResponsable not equals to DEFAULT_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("adresseResponsable.notEquals=" + DEFAULT_ADRESSE_RESPONSABLE);

        // Get all the responsableRestaurantList where adresseResponsable not equals to UPDATED_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("adresseResponsable.notEquals=" + UPDATED_ADRESSE_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByAdresseResponsableIsInShouldWork() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where adresseResponsable in DEFAULT_ADRESSE_RESPONSABLE or UPDATED_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound(
            "adresseResponsable.in=" + DEFAULT_ADRESSE_RESPONSABLE + "," + UPDATED_ADRESSE_RESPONSABLE
        );

        // Get all the responsableRestaurantList where adresseResponsable equals to UPDATED_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("adresseResponsable.in=" + UPDATED_ADRESSE_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByAdresseResponsableIsNullOrNotNull() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where adresseResponsable is not null
        defaultResponsableRestaurantShouldBeFound("adresseResponsable.specified=true");

        // Get all the responsableRestaurantList where adresseResponsable is null
        defaultResponsableRestaurantShouldNotBeFound("adresseResponsable.specified=false");
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByAdresseResponsableContainsSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where adresseResponsable contains DEFAULT_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("adresseResponsable.contains=" + DEFAULT_ADRESSE_RESPONSABLE);

        // Get all the responsableRestaurantList where adresseResponsable contains UPDATED_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("adresseResponsable.contains=" + UPDATED_ADRESSE_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByAdresseResponsableNotContainsSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where adresseResponsable does not contain DEFAULT_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("adresseResponsable.doesNotContain=" + DEFAULT_ADRESSE_RESPONSABLE);

        // Get all the responsableRestaurantList where adresseResponsable does not contain UPDATED_ADRESSE_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("adresseResponsable.doesNotContain=" + UPDATED_ADRESSE_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNumResponsableIsEqualToSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where numResponsable equals to DEFAULT_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("numResponsable.equals=" + DEFAULT_NUM_RESPONSABLE);

        // Get all the responsableRestaurantList where numResponsable equals to UPDATED_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("numResponsable.equals=" + UPDATED_NUM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNumResponsableIsNotEqualToSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where numResponsable not equals to DEFAULT_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("numResponsable.notEquals=" + DEFAULT_NUM_RESPONSABLE);

        // Get all the responsableRestaurantList where numResponsable not equals to UPDATED_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("numResponsable.notEquals=" + UPDATED_NUM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNumResponsableIsInShouldWork() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where numResponsable in DEFAULT_NUM_RESPONSABLE or UPDATED_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("numResponsable.in=" + DEFAULT_NUM_RESPONSABLE + "," + UPDATED_NUM_RESPONSABLE);

        // Get all the responsableRestaurantList where numResponsable equals to UPDATED_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("numResponsable.in=" + UPDATED_NUM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNumResponsableIsNullOrNotNull() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where numResponsable is not null
        defaultResponsableRestaurantShouldBeFound("numResponsable.specified=true");

        // Get all the responsableRestaurantList where numResponsable is null
        defaultResponsableRestaurantShouldNotBeFound("numResponsable.specified=false");
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNumResponsableContainsSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where numResponsable contains DEFAULT_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("numResponsable.contains=" + DEFAULT_NUM_RESPONSABLE);

        // Get all the responsableRestaurantList where numResponsable contains UPDATED_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("numResponsable.contains=" + UPDATED_NUM_RESPONSABLE);
    }

    @Test
    @Transactional
    void getAllResponsableRestaurantsByNumResponsableNotContainsSomething() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        // Get all the responsableRestaurantList where numResponsable does not contain DEFAULT_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldNotBeFound("numResponsable.doesNotContain=" + DEFAULT_NUM_RESPONSABLE);

        // Get all the responsableRestaurantList where numResponsable does not contain UPDATED_NUM_RESPONSABLE
        defaultResponsableRestaurantShouldBeFound("numResponsable.doesNotContain=" + UPDATED_NUM_RESPONSABLE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultResponsableRestaurantShouldBeFound(String filter) throws Exception {
        restResponsableRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(responsableRestaurant.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomResponsable").value(hasItem(DEFAULT_NOM_RESPONSABLE)))
            .andExpect(jsonPath("$.[*].prenomResponsable").value(hasItem(DEFAULT_PRENOM_RESPONSABLE)))
            .andExpect(jsonPath("$.[*].adresseResponsable").value(hasItem(DEFAULT_ADRESSE_RESPONSABLE)))
            .andExpect(jsonPath("$.[*].numResponsable").value(hasItem(DEFAULT_NUM_RESPONSABLE)));

        // Check, that the count call also returns 1
        restResponsableRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultResponsableRestaurantShouldNotBeFound(String filter) throws Exception {
        restResponsableRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restResponsableRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingResponsableRestaurant() throws Exception {
        // Get the responsableRestaurant
        restResponsableRestaurantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewResponsableRestaurant() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        int databaseSizeBeforeUpdate = responsableRestaurantRepository.findAll().size();

        // Update the responsableRestaurant
        ResponsableRestaurant updatedResponsableRestaurant = responsableRestaurantRepository.findById(responsableRestaurant.getId()).get();
        // Disconnect from session so that the updates on updatedResponsableRestaurant are not directly saved in db
        em.detach(updatedResponsableRestaurant);
        updatedResponsableRestaurant
            .nomResponsable(UPDATED_NOM_RESPONSABLE)
            .prenomResponsable(UPDATED_PRENOM_RESPONSABLE)
            .adresseResponsable(UPDATED_ADRESSE_RESPONSABLE)
            .numResponsable(UPDATED_NUM_RESPONSABLE);
        ResponsableRestaurantDTO responsableRestaurantDTO = responsableRestaurantMapper.toDto(updatedResponsableRestaurant);

        restResponsableRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, responsableRestaurantDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(responsableRestaurantDTO))
            )
            .andExpect(status().isOk());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeUpdate);
        ResponsableRestaurant testResponsableRestaurant = responsableRestaurantList.get(responsableRestaurantList.size() - 1);
        assertThat(testResponsableRestaurant.getNomResponsable()).isEqualTo(UPDATED_NOM_RESPONSABLE);
        assertThat(testResponsableRestaurant.getPrenomResponsable()).isEqualTo(UPDATED_PRENOM_RESPONSABLE);
        assertThat(testResponsableRestaurant.getAdresseResponsable()).isEqualTo(UPDATED_ADRESSE_RESPONSABLE);
        assertThat(testResponsableRestaurant.getNumResponsable()).isEqualTo(UPDATED_NUM_RESPONSABLE);
    }

    @Test
    @Transactional
    void putNonExistingResponsableRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = responsableRestaurantRepository.findAll().size();
        responsableRestaurant.setId(count.incrementAndGet());

        // Create the ResponsableRestaurant
        ResponsableRestaurantDTO responsableRestaurantDTO = responsableRestaurantMapper.toDto(responsableRestaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResponsableRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, responsableRestaurantDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(responsableRestaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchResponsableRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = responsableRestaurantRepository.findAll().size();
        responsableRestaurant.setId(count.incrementAndGet());

        // Create the ResponsableRestaurant
        ResponsableRestaurantDTO responsableRestaurantDTO = responsableRestaurantMapper.toDto(responsableRestaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResponsableRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(responsableRestaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamResponsableRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = responsableRestaurantRepository.findAll().size();
        responsableRestaurant.setId(count.incrementAndGet());

        // Create the ResponsableRestaurant
        ResponsableRestaurantDTO responsableRestaurantDTO = responsableRestaurantMapper.toDto(responsableRestaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResponsableRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(responsableRestaurantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateResponsableRestaurantWithPatch() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        int databaseSizeBeforeUpdate = responsableRestaurantRepository.findAll().size();

        // Update the responsableRestaurant using partial update
        ResponsableRestaurant partialUpdatedResponsableRestaurant = new ResponsableRestaurant();
        partialUpdatedResponsableRestaurant.setId(responsableRestaurant.getId());

        partialUpdatedResponsableRestaurant
            .nomResponsable(UPDATED_NOM_RESPONSABLE)
            .adresseResponsable(UPDATED_ADRESSE_RESPONSABLE)
            .numResponsable(UPDATED_NUM_RESPONSABLE);

        restResponsableRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResponsableRestaurant.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResponsableRestaurant))
            )
            .andExpect(status().isOk());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeUpdate);
        ResponsableRestaurant testResponsableRestaurant = responsableRestaurantList.get(responsableRestaurantList.size() - 1);
        assertThat(testResponsableRestaurant.getNomResponsable()).isEqualTo(UPDATED_NOM_RESPONSABLE);
        assertThat(testResponsableRestaurant.getPrenomResponsable()).isEqualTo(DEFAULT_PRENOM_RESPONSABLE);
        assertThat(testResponsableRestaurant.getAdresseResponsable()).isEqualTo(UPDATED_ADRESSE_RESPONSABLE);
        assertThat(testResponsableRestaurant.getNumResponsable()).isEqualTo(UPDATED_NUM_RESPONSABLE);
    }

    @Test
    @Transactional
    void fullUpdateResponsableRestaurantWithPatch() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        int databaseSizeBeforeUpdate = responsableRestaurantRepository.findAll().size();

        // Update the responsableRestaurant using partial update
        ResponsableRestaurant partialUpdatedResponsableRestaurant = new ResponsableRestaurant();
        partialUpdatedResponsableRestaurant.setId(responsableRestaurant.getId());

        partialUpdatedResponsableRestaurant
            .nomResponsable(UPDATED_NOM_RESPONSABLE)
            .prenomResponsable(UPDATED_PRENOM_RESPONSABLE)
            .adresseResponsable(UPDATED_ADRESSE_RESPONSABLE)
            .numResponsable(UPDATED_NUM_RESPONSABLE);

        restResponsableRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedResponsableRestaurant.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedResponsableRestaurant))
            )
            .andExpect(status().isOk());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeUpdate);
        ResponsableRestaurant testResponsableRestaurant = responsableRestaurantList.get(responsableRestaurantList.size() - 1);
        assertThat(testResponsableRestaurant.getNomResponsable()).isEqualTo(UPDATED_NOM_RESPONSABLE);
        assertThat(testResponsableRestaurant.getPrenomResponsable()).isEqualTo(UPDATED_PRENOM_RESPONSABLE);
        assertThat(testResponsableRestaurant.getAdresseResponsable()).isEqualTo(UPDATED_ADRESSE_RESPONSABLE);
        assertThat(testResponsableRestaurant.getNumResponsable()).isEqualTo(UPDATED_NUM_RESPONSABLE);
    }

    @Test
    @Transactional
    void patchNonExistingResponsableRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = responsableRestaurantRepository.findAll().size();
        responsableRestaurant.setId(count.incrementAndGet());

        // Create the ResponsableRestaurant
        ResponsableRestaurantDTO responsableRestaurantDTO = responsableRestaurantMapper.toDto(responsableRestaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restResponsableRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, responsableRestaurantDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(responsableRestaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchResponsableRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = responsableRestaurantRepository.findAll().size();
        responsableRestaurant.setId(count.incrementAndGet());

        // Create the ResponsableRestaurant
        ResponsableRestaurantDTO responsableRestaurantDTO = responsableRestaurantMapper.toDto(responsableRestaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResponsableRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(responsableRestaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamResponsableRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = responsableRestaurantRepository.findAll().size();
        responsableRestaurant.setId(count.incrementAndGet());

        // Create the ResponsableRestaurant
        ResponsableRestaurantDTO responsableRestaurantDTO = responsableRestaurantMapper.toDto(responsableRestaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restResponsableRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(responsableRestaurantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ResponsableRestaurant in the database
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteResponsableRestaurant() throws Exception {
        // Initialize the database
        responsableRestaurantRepository.saveAndFlush(responsableRestaurant);

        int databaseSizeBeforeDelete = responsableRestaurantRepository.findAll().size();

        // Delete the responsableRestaurant
        restResponsableRestaurantMockMvc
            .perform(delete(ENTITY_API_URL_ID, responsableRestaurant.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ResponsableRestaurant> responsableRestaurantList = responsableRestaurantRepository.findAll();
        assertThat(responsableRestaurantList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
