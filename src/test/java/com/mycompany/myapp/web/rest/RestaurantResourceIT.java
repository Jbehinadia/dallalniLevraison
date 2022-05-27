package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.ResponsableRestaurant;
import com.mycompany.myapp.domain.Restaurant;
import com.mycompany.myapp.repository.RestaurantRepository;
import com.mycompany.myapp.service.criteria.RestaurantCriteria;
import com.mycompany.myapp.service.dto.RestaurantDTO;
import com.mycompany.myapp.service.mapper.RestaurantMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link RestaurantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RestaurantResourceIT {

    private static final String DEFAULT_NOM_RESTAURANT = "AAAAAAAAAA";
    private static final String UPDATED_NOM_RESTAURANT = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE_RESTAURANT = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE_RESTAURANT = "BBBBBBBBBB";

    private static final String DEFAULT_NUM_RESTAURANT = "AAAAAAAAAA";
    private static final String UPDATED_NUM_RESTAURANT = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_OUVERTURE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_OUVERTURE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_FERMITURE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_FERMITURE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/restaurants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantMapper restaurantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRestaurantMockMvc;

    private Restaurant restaurant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurant createEntity(EntityManager em) {
        Restaurant restaurant = new Restaurant()
            .nomRestaurant(DEFAULT_NOM_RESTAURANT)
            .adresseRestaurant(DEFAULT_ADRESSE_RESTAURANT)
            .numRestaurant(DEFAULT_NUM_RESTAURANT)
            .dateOuverture(DEFAULT_DATE_OUVERTURE)
            .dateFermiture(DEFAULT_DATE_FERMITURE);
        return restaurant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurant createUpdatedEntity(EntityManager em) {
        Restaurant restaurant = new Restaurant()
            .nomRestaurant(UPDATED_NOM_RESTAURANT)
            .adresseRestaurant(UPDATED_ADRESSE_RESTAURANT)
            .numRestaurant(UPDATED_NUM_RESTAURANT)
            .dateOuverture(UPDATED_DATE_OUVERTURE)
            .dateFermiture(UPDATED_DATE_FERMITURE);
        return restaurant;
    }

    @BeforeEach
    public void initTest() {
        restaurant = createEntity(em);
    }

    @Test
    @Transactional
    void createRestaurant() throws Exception {
        int databaseSizeBeforeCreate = restaurantRepository.findAll().size();
        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);
        restRestaurantMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate + 1);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getNomRestaurant()).isEqualTo(DEFAULT_NOM_RESTAURANT);
        assertThat(testRestaurant.getAdresseRestaurant()).isEqualTo(DEFAULT_ADRESSE_RESTAURANT);
        assertThat(testRestaurant.getNumRestaurant()).isEqualTo(DEFAULT_NUM_RESTAURANT);
        assertThat(testRestaurant.getDateOuverture()).isEqualTo(DEFAULT_DATE_OUVERTURE);
        assertThat(testRestaurant.getDateFermiture()).isEqualTo(DEFAULT_DATE_FERMITURE);
    }

    @Test
    @Transactional
    void createRestaurantWithExistingId() throws Exception {
        // Create the Restaurant with an existing ID
        restaurant.setId(1L);
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        int databaseSizeBeforeCreate = restaurantRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRestaurantMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRestaurants() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurant.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomRestaurant").value(hasItem(DEFAULT_NOM_RESTAURANT)))
            .andExpect(jsonPath("$.[*].adresseRestaurant").value(hasItem(DEFAULT_ADRESSE_RESTAURANT)))
            .andExpect(jsonPath("$.[*].numRestaurant").value(hasItem(DEFAULT_NUM_RESTAURANT)))
            .andExpect(jsonPath("$.[*].dateOuverture").value(hasItem(DEFAULT_DATE_OUVERTURE.toString())))
            .andExpect(jsonPath("$.[*].dateFermiture").value(hasItem(DEFAULT_DATE_FERMITURE.toString())));
    }

    @Test
    @Transactional
    void getRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get the restaurant
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL_ID, restaurant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(restaurant.getId().intValue()))
            .andExpect(jsonPath("$.nomRestaurant").value(DEFAULT_NOM_RESTAURANT))
            .andExpect(jsonPath("$.adresseRestaurant").value(DEFAULT_ADRESSE_RESTAURANT))
            .andExpect(jsonPath("$.numRestaurant").value(DEFAULT_NUM_RESTAURANT))
            .andExpect(jsonPath("$.dateOuverture").value(DEFAULT_DATE_OUVERTURE.toString()))
            .andExpect(jsonPath("$.dateFermiture").value(DEFAULT_DATE_FERMITURE.toString()));
    }

    @Test
    @Transactional
    void getRestaurantsByIdFiltering() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        Long id = restaurant.getId();

        defaultRestaurantShouldBeFound("id.equals=" + id);
        defaultRestaurantShouldNotBeFound("id.notEquals=" + id);

        defaultRestaurantShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRestaurantShouldNotBeFound("id.greaterThan=" + id);

        defaultRestaurantShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRestaurantShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestaurantIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomRestaurant equals to DEFAULT_NOM_RESTAURANT
        defaultRestaurantShouldBeFound("nomRestaurant.equals=" + DEFAULT_NOM_RESTAURANT);

        // Get all the restaurantList where nomRestaurant equals to UPDATED_NOM_RESTAURANT
        defaultRestaurantShouldNotBeFound("nomRestaurant.equals=" + UPDATED_NOM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestaurantIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomRestaurant not equals to DEFAULT_NOM_RESTAURANT
        defaultRestaurantShouldNotBeFound("nomRestaurant.notEquals=" + DEFAULT_NOM_RESTAURANT);

        // Get all the restaurantList where nomRestaurant not equals to UPDATED_NOM_RESTAURANT
        defaultRestaurantShouldBeFound("nomRestaurant.notEquals=" + UPDATED_NOM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestaurantIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomRestaurant in DEFAULT_NOM_RESTAURANT or UPDATED_NOM_RESTAURANT
        defaultRestaurantShouldBeFound("nomRestaurant.in=" + DEFAULT_NOM_RESTAURANT + "," + UPDATED_NOM_RESTAURANT);

        // Get all the restaurantList where nomRestaurant equals to UPDATED_NOM_RESTAURANT
        defaultRestaurantShouldNotBeFound("nomRestaurant.in=" + UPDATED_NOM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestaurantIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomRestaurant is not null
        defaultRestaurantShouldBeFound("nomRestaurant.specified=true");

        // Get all the restaurantList where nomRestaurant is null
        defaultRestaurantShouldNotBeFound("nomRestaurant.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestaurantContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomRestaurant contains DEFAULT_NOM_RESTAURANT
        defaultRestaurantShouldBeFound("nomRestaurant.contains=" + DEFAULT_NOM_RESTAURANT);

        // Get all the restaurantList where nomRestaurant contains UPDATED_NOM_RESTAURANT
        defaultRestaurantShouldNotBeFound("nomRestaurant.contains=" + UPDATED_NOM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNomRestaurantNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where nomRestaurant does not contain DEFAULT_NOM_RESTAURANT
        defaultRestaurantShouldNotBeFound("nomRestaurant.doesNotContain=" + DEFAULT_NOM_RESTAURANT);

        // Get all the restaurantList where nomRestaurant does not contain UPDATED_NOM_RESTAURANT
        defaultRestaurantShouldBeFound("nomRestaurant.doesNotContain=" + UPDATED_NOM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestaurantIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseRestaurant equals to DEFAULT_ADRESSE_RESTAURANT
        defaultRestaurantShouldBeFound("adresseRestaurant.equals=" + DEFAULT_ADRESSE_RESTAURANT);

        // Get all the restaurantList where adresseRestaurant equals to UPDATED_ADRESSE_RESTAURANT
        defaultRestaurantShouldNotBeFound("adresseRestaurant.equals=" + UPDATED_ADRESSE_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestaurantIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseRestaurant not equals to DEFAULT_ADRESSE_RESTAURANT
        defaultRestaurantShouldNotBeFound("adresseRestaurant.notEquals=" + DEFAULT_ADRESSE_RESTAURANT);

        // Get all the restaurantList where adresseRestaurant not equals to UPDATED_ADRESSE_RESTAURANT
        defaultRestaurantShouldBeFound("adresseRestaurant.notEquals=" + UPDATED_ADRESSE_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestaurantIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseRestaurant in DEFAULT_ADRESSE_RESTAURANT or UPDATED_ADRESSE_RESTAURANT
        defaultRestaurantShouldBeFound("adresseRestaurant.in=" + DEFAULT_ADRESSE_RESTAURANT + "," + UPDATED_ADRESSE_RESTAURANT);

        // Get all the restaurantList where adresseRestaurant equals to UPDATED_ADRESSE_RESTAURANT
        defaultRestaurantShouldNotBeFound("adresseRestaurant.in=" + UPDATED_ADRESSE_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestaurantIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseRestaurant is not null
        defaultRestaurantShouldBeFound("adresseRestaurant.specified=true");

        // Get all the restaurantList where adresseRestaurant is null
        defaultRestaurantShouldNotBeFound("adresseRestaurant.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestaurantContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseRestaurant contains DEFAULT_ADRESSE_RESTAURANT
        defaultRestaurantShouldBeFound("adresseRestaurant.contains=" + DEFAULT_ADRESSE_RESTAURANT);

        // Get all the restaurantList where adresseRestaurant contains UPDATED_ADRESSE_RESTAURANT
        defaultRestaurantShouldNotBeFound("adresseRestaurant.contains=" + UPDATED_ADRESSE_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByAdresseRestaurantNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where adresseRestaurant does not contain DEFAULT_ADRESSE_RESTAURANT
        defaultRestaurantShouldNotBeFound("adresseRestaurant.doesNotContain=" + DEFAULT_ADRESSE_RESTAURANT);

        // Get all the restaurantList where adresseRestaurant does not contain UPDATED_ADRESSE_RESTAURANT
        defaultRestaurantShouldBeFound("adresseRestaurant.doesNotContain=" + UPDATED_ADRESSE_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNumRestaurantIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where numRestaurant equals to DEFAULT_NUM_RESTAURANT
        defaultRestaurantShouldBeFound("numRestaurant.equals=" + DEFAULT_NUM_RESTAURANT);

        // Get all the restaurantList where numRestaurant equals to UPDATED_NUM_RESTAURANT
        defaultRestaurantShouldNotBeFound("numRestaurant.equals=" + UPDATED_NUM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNumRestaurantIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where numRestaurant not equals to DEFAULT_NUM_RESTAURANT
        defaultRestaurantShouldNotBeFound("numRestaurant.notEquals=" + DEFAULT_NUM_RESTAURANT);

        // Get all the restaurantList where numRestaurant not equals to UPDATED_NUM_RESTAURANT
        defaultRestaurantShouldBeFound("numRestaurant.notEquals=" + UPDATED_NUM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNumRestaurantIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where numRestaurant in DEFAULT_NUM_RESTAURANT or UPDATED_NUM_RESTAURANT
        defaultRestaurantShouldBeFound("numRestaurant.in=" + DEFAULT_NUM_RESTAURANT + "," + UPDATED_NUM_RESTAURANT);

        // Get all the restaurantList where numRestaurant equals to UPDATED_NUM_RESTAURANT
        defaultRestaurantShouldNotBeFound("numRestaurant.in=" + UPDATED_NUM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNumRestaurantIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where numRestaurant is not null
        defaultRestaurantShouldBeFound("numRestaurant.specified=true");

        // Get all the restaurantList where numRestaurant is null
        defaultRestaurantShouldNotBeFound("numRestaurant.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByNumRestaurantContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where numRestaurant contains DEFAULT_NUM_RESTAURANT
        defaultRestaurantShouldBeFound("numRestaurant.contains=" + DEFAULT_NUM_RESTAURANT);

        // Get all the restaurantList where numRestaurant contains UPDATED_NUM_RESTAURANT
        defaultRestaurantShouldNotBeFound("numRestaurant.contains=" + UPDATED_NUM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByNumRestaurantNotContainsSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where numRestaurant does not contain DEFAULT_NUM_RESTAURANT
        defaultRestaurantShouldNotBeFound("numRestaurant.doesNotContain=" + DEFAULT_NUM_RESTAURANT);

        // Get all the restaurantList where numRestaurant does not contain UPDATED_NUM_RESTAURANT
        defaultRestaurantShouldBeFound("numRestaurant.doesNotContain=" + UPDATED_NUM_RESTAURANT);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDateOuvertureIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where dateOuverture equals to DEFAULT_DATE_OUVERTURE
        defaultRestaurantShouldBeFound("dateOuverture.equals=" + DEFAULT_DATE_OUVERTURE);

        // Get all the restaurantList where dateOuverture equals to UPDATED_DATE_OUVERTURE
        defaultRestaurantShouldNotBeFound("dateOuverture.equals=" + UPDATED_DATE_OUVERTURE);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDateOuvertureIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where dateOuverture not equals to DEFAULT_DATE_OUVERTURE
        defaultRestaurantShouldNotBeFound("dateOuverture.notEquals=" + DEFAULT_DATE_OUVERTURE);

        // Get all the restaurantList where dateOuverture not equals to UPDATED_DATE_OUVERTURE
        defaultRestaurantShouldBeFound("dateOuverture.notEquals=" + UPDATED_DATE_OUVERTURE);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDateOuvertureIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where dateOuverture in DEFAULT_DATE_OUVERTURE or UPDATED_DATE_OUVERTURE
        defaultRestaurantShouldBeFound("dateOuverture.in=" + DEFAULT_DATE_OUVERTURE + "," + UPDATED_DATE_OUVERTURE);

        // Get all the restaurantList where dateOuverture equals to UPDATED_DATE_OUVERTURE
        defaultRestaurantShouldNotBeFound("dateOuverture.in=" + UPDATED_DATE_OUVERTURE);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDateOuvertureIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where dateOuverture is not null
        defaultRestaurantShouldBeFound("dateOuverture.specified=true");

        // Get all the restaurantList where dateOuverture is null
        defaultRestaurantShouldNotBeFound("dateOuverture.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByDateFermitureIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where dateFermiture equals to DEFAULT_DATE_FERMITURE
        defaultRestaurantShouldBeFound("dateFermiture.equals=" + DEFAULT_DATE_FERMITURE);

        // Get all the restaurantList where dateFermiture equals to UPDATED_DATE_FERMITURE
        defaultRestaurantShouldNotBeFound("dateFermiture.equals=" + UPDATED_DATE_FERMITURE);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDateFermitureIsNotEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where dateFermiture not equals to DEFAULT_DATE_FERMITURE
        defaultRestaurantShouldNotBeFound("dateFermiture.notEquals=" + DEFAULT_DATE_FERMITURE);

        // Get all the restaurantList where dateFermiture not equals to UPDATED_DATE_FERMITURE
        defaultRestaurantShouldBeFound("dateFermiture.notEquals=" + UPDATED_DATE_FERMITURE);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDateFermitureIsInShouldWork() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where dateFermiture in DEFAULT_DATE_FERMITURE or UPDATED_DATE_FERMITURE
        defaultRestaurantShouldBeFound("dateFermiture.in=" + DEFAULT_DATE_FERMITURE + "," + UPDATED_DATE_FERMITURE);

        // Get all the restaurantList where dateFermiture equals to UPDATED_DATE_FERMITURE
        defaultRestaurantShouldNotBeFound("dateFermiture.in=" + UPDATED_DATE_FERMITURE);
    }

    @Test
    @Transactional
    void getAllRestaurantsByDateFermitureIsNullOrNotNull() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList where dateFermiture is not null
        defaultRestaurantShouldBeFound("dateFermiture.specified=true");

        // Get all the restaurantList where dateFermiture is null
        defaultRestaurantShouldNotBeFound("dateFermiture.specified=false");
    }

    @Test
    @Transactional
    void getAllRestaurantsByResponsableRestaurantIsEqualToSomething() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);
        ResponsableRestaurant responsableRestaurant;
        if (TestUtil.findAll(em, ResponsableRestaurant.class).isEmpty()) {
            responsableRestaurant = ResponsableRestaurantResourceIT.createEntity(em);
            em.persist(responsableRestaurant);
            em.flush();
        } else {
            responsableRestaurant = TestUtil.findAll(em, ResponsableRestaurant.class).get(0);
        }
        em.persist(responsableRestaurant);
        em.flush();
        restaurant.setResponsableRestaurant(responsableRestaurant);
        restaurantRepository.saveAndFlush(restaurant);
        Long responsableRestaurantId = responsableRestaurant.getId();

        // Get all the restaurantList where responsableRestaurant equals to responsableRestaurantId
        defaultRestaurantShouldBeFound("responsableRestaurantId.equals=" + responsableRestaurantId);

        // Get all the restaurantList where responsableRestaurant equals to (responsableRestaurantId + 1)
        defaultRestaurantShouldNotBeFound("responsableRestaurantId.equals=" + (responsableRestaurantId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRestaurantShouldBeFound(String filter) throws Exception {
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurant.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomRestaurant").value(hasItem(DEFAULT_NOM_RESTAURANT)))
            .andExpect(jsonPath("$.[*].adresseRestaurant").value(hasItem(DEFAULT_ADRESSE_RESTAURANT)))
            .andExpect(jsonPath("$.[*].numRestaurant").value(hasItem(DEFAULT_NUM_RESTAURANT)))
            .andExpect(jsonPath("$.[*].dateOuverture").value(hasItem(DEFAULT_DATE_OUVERTURE.toString())))
            .andExpect(jsonPath("$.[*].dateFermiture").value(hasItem(DEFAULT_DATE_FERMITURE.toString())));

        // Check, that the count call also returns 1
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRestaurantShouldNotBeFound(String filter) throws Exception {
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRestaurantMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRestaurant() throws Exception {
        // Get the restaurant
        restRestaurantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant
        Restaurant updatedRestaurant = restaurantRepository.findById(restaurant.getId()).get();
        // Disconnect from session so that the updates on updatedRestaurant are not directly saved in db
        em.detach(updatedRestaurant);
        updatedRestaurant
            .nomRestaurant(UPDATED_NOM_RESTAURANT)
            .adresseRestaurant(UPDATED_ADRESSE_RESTAURANT)
            .numRestaurant(UPDATED_NUM_RESTAURANT)
            .dateOuverture(UPDATED_DATE_OUVERTURE)
            .dateFermiture(UPDATED_DATE_FERMITURE);
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(updatedRestaurant);

        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getNomRestaurant()).isEqualTo(UPDATED_NOM_RESTAURANT);
        assertThat(testRestaurant.getAdresseRestaurant()).isEqualTo(UPDATED_ADRESSE_RESTAURANT);
        assertThat(testRestaurant.getNumRestaurant()).isEqualTo(UPDATED_NUM_RESTAURANT);
        assertThat(testRestaurant.getDateOuverture()).isEqualTo(UPDATED_DATE_OUVERTURE);
        assertThat(testRestaurant.getDateFermiture()).isEqualTo(UPDATED_DATE_FERMITURE);
    }

    @Test
    @Transactional
    void putNonExistingRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurantDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant using partial update
        Restaurant partialUpdatedRestaurant = new Restaurant();
        partialUpdatedRestaurant.setId(restaurant.getId());

        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurant.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurant))
            )
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getNomRestaurant()).isEqualTo(DEFAULT_NOM_RESTAURANT);
        assertThat(testRestaurant.getAdresseRestaurant()).isEqualTo(DEFAULT_ADRESSE_RESTAURANT);
        assertThat(testRestaurant.getNumRestaurant()).isEqualTo(DEFAULT_NUM_RESTAURANT);
        assertThat(testRestaurant.getDateOuverture()).isEqualTo(DEFAULT_DATE_OUVERTURE);
        assertThat(testRestaurant.getDateFermiture()).isEqualTo(DEFAULT_DATE_FERMITURE);
    }

    @Test
    @Transactional
    void fullUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant using partial update
        Restaurant partialUpdatedRestaurant = new Restaurant();
        partialUpdatedRestaurant.setId(restaurant.getId());

        partialUpdatedRestaurant
            .nomRestaurant(UPDATED_NOM_RESTAURANT)
            .adresseRestaurant(UPDATED_ADRESSE_RESTAURANT)
            .numRestaurant(UPDATED_NUM_RESTAURANT)
            .dateOuverture(UPDATED_DATE_OUVERTURE)
            .dateFermiture(UPDATED_DATE_FERMITURE);

        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurant.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurant))
            )
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getNomRestaurant()).isEqualTo(UPDATED_NOM_RESTAURANT);
        assertThat(testRestaurant.getAdresseRestaurant()).isEqualTo(UPDATED_ADRESSE_RESTAURANT);
        assertThat(testRestaurant.getNumRestaurant()).isEqualTo(UPDATED_NUM_RESTAURANT);
        assertThat(testRestaurant.getDateOuverture()).isEqualTo(UPDATED_DATE_OUVERTURE);
        assertThat(testRestaurant.getDateFermiture()).isEqualTo(UPDATED_DATE_FERMITURE);
    }

    @Test
    @Transactional
    void patchNonExistingRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, restaurantDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();
        restaurant.setId(count.incrementAndGet());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurantMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        int databaseSizeBeforeDelete = restaurantRepository.findAll().size();

        // Delete the restaurant
        restRestaurantMockMvc
            .perform(delete(ENTITY_API_URL_ID, restaurant.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
