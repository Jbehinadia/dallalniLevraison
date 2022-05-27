package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Menu;
import com.mycompany.myapp.domain.Plat;
import com.mycompany.myapp.domain.TypePlat;
import com.mycompany.myapp.repository.PlatRepository;
import com.mycompany.myapp.service.criteria.PlatCriteria;
import com.mycompany.myapp.service.dto.PlatDTO;
import com.mycompany.myapp.service.mapper.PlatMapper;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link PlatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlatResourceIT {

    private static final String DEFAULT_NOM_PLAT = "AAAAAAAAAA";
    private static final String UPDATED_NOM_PLAT = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_PATH = "BBBBBBBBBB";

    private static final Double DEFAULT_PRIX = 1D;
    private static final Double UPDATED_PRIX = 2D;
    private static final Double SMALLER_PRIX = 1D - 1D;

    private static final Double DEFAULT_REMISE_PERC = 1D;
    private static final Double UPDATED_REMISE_PERC = 2D;
    private static final Double SMALLER_REMISE_PERC = 1D - 1D;

    private static final Double DEFAULT_REMICE_VAL = 1D;
    private static final Double UPDATED_REMICE_VAL = 2D;
    private static final Double SMALLER_REMICE_VAL = 1D - 1D;

    private static final String ENTITY_API_URL = "/api/plats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlatRepository platRepository;

    @Autowired
    private PlatMapper platMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlatMockMvc;

    private Plat plat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plat createEntity(EntityManager em) {
        Plat plat = new Plat()
            .nomPlat(DEFAULT_NOM_PLAT)
            .imagePath(DEFAULT_IMAGE_PATH)
            .prix(DEFAULT_PRIX)
            .remisePerc(DEFAULT_REMISE_PERC)
            .remiceVal(DEFAULT_REMICE_VAL);
        return plat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Plat createUpdatedEntity(EntityManager em) {
        Plat plat = new Plat()
            .nomPlat(UPDATED_NOM_PLAT)
            .imagePath(UPDATED_IMAGE_PATH)
            .prix(UPDATED_PRIX)
            .remisePerc(UPDATED_REMISE_PERC)
            .remiceVal(UPDATED_REMICE_VAL);
        return plat;
    }

    @BeforeEach
    public void initTest() {
        plat = createEntity(em);
    }

    @Test
    @Transactional
    void createPlat() throws Exception {
        int databaseSizeBeforeCreate = platRepository.findAll().size();
        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);
        restPlatMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeCreate + 1);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getNomPlat()).isEqualTo(DEFAULT_NOM_PLAT);
        assertThat(testPlat.getImagePath()).isEqualTo(DEFAULT_IMAGE_PATH);
        assertThat(testPlat.getPrix()).isEqualTo(DEFAULT_PRIX);
        assertThat(testPlat.getRemisePerc()).isEqualTo(DEFAULT_REMISE_PERC);
        assertThat(testPlat.getRemiceVal()).isEqualTo(DEFAULT_REMICE_VAL);
    }

    @Test
    @Transactional
    void createPlatWithExistingId() throws Exception {
        // Create the Plat with an existing ID
        plat.setId(1L);
        PlatDTO platDTO = platMapper.toDto(plat);

        int databaseSizeBeforeCreate = platRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlatMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPlats() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plat.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomPlat").value(hasItem(DEFAULT_NOM_PLAT)))
            .andExpect(jsonPath("$.[*].imagePath").value(hasItem(DEFAULT_IMAGE_PATH.toString())))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(DEFAULT_PRIX.doubleValue())))
            .andExpect(jsonPath("$.[*].remisePerc").value(hasItem(DEFAULT_REMISE_PERC.doubleValue())))
            .andExpect(jsonPath("$.[*].remiceVal").value(hasItem(DEFAULT_REMICE_VAL.doubleValue())));
    }

    @Test
    @Transactional
    void getPlat() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get the plat
        restPlatMockMvc
            .perform(get(ENTITY_API_URL_ID, plat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(plat.getId().intValue()))
            .andExpect(jsonPath("$.nomPlat").value(DEFAULT_NOM_PLAT))
            .andExpect(jsonPath("$.imagePath").value(DEFAULT_IMAGE_PATH.toString()))
            .andExpect(jsonPath("$.prix").value(DEFAULT_PRIX.doubleValue()))
            .andExpect(jsonPath("$.remisePerc").value(DEFAULT_REMISE_PERC.doubleValue()))
            .andExpect(jsonPath("$.remiceVal").value(DEFAULT_REMICE_VAL.doubleValue()));
    }

    @Test
    @Transactional
    void getPlatsByIdFiltering() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        Long id = plat.getId();

        defaultPlatShouldBeFound("id.equals=" + id);
        defaultPlatShouldNotBeFound("id.notEquals=" + id);

        defaultPlatShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPlatShouldNotBeFound("id.greaterThan=" + id);

        defaultPlatShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPlatShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat equals to DEFAULT_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.equals=" + DEFAULT_NOM_PLAT);

        // Get all the platList where nomPlat equals to UPDATED_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.equals=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat not equals to DEFAULT_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.notEquals=" + DEFAULT_NOM_PLAT);

        // Get all the platList where nomPlat not equals to UPDATED_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.notEquals=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatIsInShouldWork() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat in DEFAULT_NOM_PLAT or UPDATED_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.in=" + DEFAULT_NOM_PLAT + "," + UPDATED_NOM_PLAT);

        // Get all the platList where nomPlat equals to UPDATED_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.in=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatIsNullOrNotNull() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat is not null
        defaultPlatShouldBeFound("nomPlat.specified=true");

        // Get all the platList where nomPlat is null
        defaultPlatShouldNotBeFound("nomPlat.specified=false");
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatContainsSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat contains DEFAULT_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.contains=" + DEFAULT_NOM_PLAT);

        // Get all the platList where nomPlat contains UPDATED_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.contains=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByNomPlatNotContainsSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where nomPlat does not contain DEFAULT_NOM_PLAT
        defaultPlatShouldNotBeFound("nomPlat.doesNotContain=" + DEFAULT_NOM_PLAT);

        // Get all the platList where nomPlat does not contain UPDATED_NOM_PLAT
        defaultPlatShouldBeFound("nomPlat.doesNotContain=" + UPDATED_NOM_PLAT);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix equals to DEFAULT_PRIX
        defaultPlatShouldBeFound("prix.equals=" + DEFAULT_PRIX);

        // Get all the platList where prix equals to UPDATED_PRIX
        defaultPlatShouldNotBeFound("prix.equals=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsNotEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix not equals to DEFAULT_PRIX
        defaultPlatShouldNotBeFound("prix.notEquals=" + DEFAULT_PRIX);

        // Get all the platList where prix not equals to UPDATED_PRIX
        defaultPlatShouldBeFound("prix.notEquals=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsInShouldWork() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix in DEFAULT_PRIX or UPDATED_PRIX
        defaultPlatShouldBeFound("prix.in=" + DEFAULT_PRIX + "," + UPDATED_PRIX);

        // Get all the platList where prix equals to UPDATED_PRIX
        defaultPlatShouldNotBeFound("prix.in=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsNullOrNotNull() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix is not null
        defaultPlatShouldBeFound("prix.specified=true");

        // Get all the platList where prix is null
        defaultPlatShouldNotBeFound("prix.specified=false");
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix is greater than or equal to DEFAULT_PRIX
        defaultPlatShouldBeFound("prix.greaterThanOrEqual=" + DEFAULT_PRIX);

        // Get all the platList where prix is greater than or equal to UPDATED_PRIX
        defaultPlatShouldNotBeFound("prix.greaterThanOrEqual=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix is less than or equal to DEFAULT_PRIX
        defaultPlatShouldBeFound("prix.lessThanOrEqual=" + DEFAULT_PRIX);

        // Get all the platList where prix is less than or equal to SMALLER_PRIX
        defaultPlatShouldNotBeFound("prix.lessThanOrEqual=" + SMALLER_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsLessThanSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix is less than DEFAULT_PRIX
        defaultPlatShouldNotBeFound("prix.lessThan=" + DEFAULT_PRIX);

        // Get all the platList where prix is less than UPDATED_PRIX
        defaultPlatShouldBeFound("prix.lessThan=" + UPDATED_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByPrixIsGreaterThanSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where prix is greater than DEFAULT_PRIX
        defaultPlatShouldNotBeFound("prix.greaterThan=" + DEFAULT_PRIX);

        // Get all the platList where prix is greater than SMALLER_PRIX
        defaultPlatShouldBeFound("prix.greaterThan=" + SMALLER_PRIX);
    }

    @Test
    @Transactional
    void getAllPlatsByRemisePercIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remisePerc equals to DEFAULT_REMISE_PERC
        defaultPlatShouldBeFound("remisePerc.equals=" + DEFAULT_REMISE_PERC);

        // Get all the platList where remisePerc equals to UPDATED_REMISE_PERC
        defaultPlatShouldNotBeFound("remisePerc.equals=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllPlatsByRemisePercIsNotEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remisePerc not equals to DEFAULT_REMISE_PERC
        defaultPlatShouldNotBeFound("remisePerc.notEquals=" + DEFAULT_REMISE_PERC);

        // Get all the platList where remisePerc not equals to UPDATED_REMISE_PERC
        defaultPlatShouldBeFound("remisePerc.notEquals=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllPlatsByRemisePercIsInShouldWork() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remisePerc in DEFAULT_REMISE_PERC or UPDATED_REMISE_PERC
        defaultPlatShouldBeFound("remisePerc.in=" + DEFAULT_REMISE_PERC + "," + UPDATED_REMISE_PERC);

        // Get all the platList where remisePerc equals to UPDATED_REMISE_PERC
        defaultPlatShouldNotBeFound("remisePerc.in=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllPlatsByRemisePercIsNullOrNotNull() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remisePerc is not null
        defaultPlatShouldBeFound("remisePerc.specified=true");

        // Get all the platList where remisePerc is null
        defaultPlatShouldNotBeFound("remisePerc.specified=false");
    }

    @Test
    @Transactional
    void getAllPlatsByRemisePercIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remisePerc is greater than or equal to DEFAULT_REMISE_PERC
        defaultPlatShouldBeFound("remisePerc.greaterThanOrEqual=" + DEFAULT_REMISE_PERC);

        // Get all the platList where remisePerc is greater than or equal to UPDATED_REMISE_PERC
        defaultPlatShouldNotBeFound("remisePerc.greaterThanOrEqual=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllPlatsByRemisePercIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remisePerc is less than or equal to DEFAULT_REMISE_PERC
        defaultPlatShouldBeFound("remisePerc.lessThanOrEqual=" + DEFAULT_REMISE_PERC);

        // Get all the platList where remisePerc is less than or equal to SMALLER_REMISE_PERC
        defaultPlatShouldNotBeFound("remisePerc.lessThanOrEqual=" + SMALLER_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllPlatsByRemisePercIsLessThanSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remisePerc is less than DEFAULT_REMISE_PERC
        defaultPlatShouldNotBeFound("remisePerc.lessThan=" + DEFAULT_REMISE_PERC);

        // Get all the platList where remisePerc is less than UPDATED_REMISE_PERC
        defaultPlatShouldBeFound("remisePerc.lessThan=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllPlatsByRemisePercIsGreaterThanSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remisePerc is greater than DEFAULT_REMISE_PERC
        defaultPlatShouldNotBeFound("remisePerc.greaterThan=" + DEFAULT_REMISE_PERC);

        // Get all the platList where remisePerc is greater than SMALLER_REMISE_PERC
        defaultPlatShouldBeFound("remisePerc.greaterThan=" + SMALLER_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllPlatsByRemiceValIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remiceVal equals to DEFAULT_REMICE_VAL
        defaultPlatShouldBeFound("remiceVal.equals=" + DEFAULT_REMICE_VAL);

        // Get all the platList where remiceVal equals to UPDATED_REMICE_VAL
        defaultPlatShouldNotBeFound("remiceVal.equals=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllPlatsByRemiceValIsNotEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remiceVal not equals to DEFAULT_REMICE_VAL
        defaultPlatShouldNotBeFound("remiceVal.notEquals=" + DEFAULT_REMICE_VAL);

        // Get all the platList where remiceVal not equals to UPDATED_REMICE_VAL
        defaultPlatShouldBeFound("remiceVal.notEquals=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllPlatsByRemiceValIsInShouldWork() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remiceVal in DEFAULT_REMICE_VAL or UPDATED_REMICE_VAL
        defaultPlatShouldBeFound("remiceVal.in=" + DEFAULT_REMICE_VAL + "," + UPDATED_REMICE_VAL);

        // Get all the platList where remiceVal equals to UPDATED_REMICE_VAL
        defaultPlatShouldNotBeFound("remiceVal.in=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllPlatsByRemiceValIsNullOrNotNull() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remiceVal is not null
        defaultPlatShouldBeFound("remiceVal.specified=true");

        // Get all the platList where remiceVal is null
        defaultPlatShouldNotBeFound("remiceVal.specified=false");
    }

    @Test
    @Transactional
    void getAllPlatsByRemiceValIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remiceVal is greater than or equal to DEFAULT_REMICE_VAL
        defaultPlatShouldBeFound("remiceVal.greaterThanOrEqual=" + DEFAULT_REMICE_VAL);

        // Get all the platList where remiceVal is greater than or equal to UPDATED_REMICE_VAL
        defaultPlatShouldNotBeFound("remiceVal.greaterThanOrEqual=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllPlatsByRemiceValIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remiceVal is less than or equal to DEFAULT_REMICE_VAL
        defaultPlatShouldBeFound("remiceVal.lessThanOrEqual=" + DEFAULT_REMICE_VAL);

        // Get all the platList where remiceVal is less than or equal to SMALLER_REMICE_VAL
        defaultPlatShouldNotBeFound("remiceVal.lessThanOrEqual=" + SMALLER_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllPlatsByRemiceValIsLessThanSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remiceVal is less than DEFAULT_REMICE_VAL
        defaultPlatShouldNotBeFound("remiceVal.lessThan=" + DEFAULT_REMICE_VAL);

        // Get all the platList where remiceVal is less than UPDATED_REMICE_VAL
        defaultPlatShouldBeFound("remiceVal.lessThan=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllPlatsByRemiceValIsGreaterThanSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        // Get all the platList where remiceVal is greater than DEFAULT_REMICE_VAL
        defaultPlatShouldNotBeFound("remiceVal.greaterThan=" + DEFAULT_REMICE_VAL);

        // Get all the platList where remiceVal is greater than SMALLER_REMICE_VAL
        defaultPlatShouldBeFound("remiceVal.greaterThan=" + SMALLER_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllPlatsByMenuIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);
        Menu menu;
        if (TestUtil.findAll(em, Menu.class).isEmpty()) {
            menu = MenuResourceIT.createEntity(em);
            em.persist(menu);
            em.flush();
        } else {
            menu = TestUtil.findAll(em, Menu.class).get(0);
        }
        em.persist(menu);
        em.flush();
        plat.setMenu(menu);
        platRepository.saveAndFlush(plat);
        Long menuId = menu.getId();

        // Get all the platList where menu equals to menuId
        defaultPlatShouldBeFound("menuId.equals=" + menuId);

        // Get all the platList where menu equals to (menuId + 1)
        defaultPlatShouldNotBeFound("menuId.equals=" + (menuId + 1));
    }

    @Test
    @Transactional
    void getAllPlatsByTypePlatIsEqualToSomething() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);
        TypePlat typePlat;
        if (TestUtil.findAll(em, TypePlat.class).isEmpty()) {
            typePlat = TypePlatResourceIT.createEntity(em);
            em.persist(typePlat);
            em.flush();
        } else {
            typePlat = TestUtil.findAll(em, TypePlat.class).get(0);
        }
        em.persist(typePlat);
        em.flush();
        plat.setTypePlat(typePlat);
        platRepository.saveAndFlush(plat);
        Long typePlatId = typePlat.getId();

        // Get all the platList where typePlat equals to typePlatId
        defaultPlatShouldBeFound("typePlatId.equals=" + typePlatId);

        // Get all the platList where typePlat equals to (typePlatId + 1)
        defaultPlatShouldNotBeFound("typePlatId.equals=" + (typePlatId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPlatShouldBeFound(String filter) throws Exception {
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(plat.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomPlat").value(hasItem(DEFAULT_NOM_PLAT)))
            .andExpect(jsonPath("$.[*].imagePath").value(hasItem(DEFAULT_IMAGE_PATH.toString())))
            .andExpect(jsonPath("$.[*].prix").value(hasItem(DEFAULT_PRIX.doubleValue())))
            .andExpect(jsonPath("$.[*].remisePerc").value(hasItem(DEFAULT_REMISE_PERC.doubleValue())))
            .andExpect(jsonPath("$.[*].remiceVal").value(hasItem(DEFAULT_REMICE_VAL.doubleValue())));

        // Check, that the count call also returns 1
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPlatShouldNotBeFound(String filter) throws Exception {
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPlatMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPlat() throws Exception {
        // Get the plat
        restPlatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPlat() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeUpdate = platRepository.findAll().size();

        // Update the plat
        Plat updatedPlat = platRepository.findById(plat.getId()).get();
        // Disconnect from session so that the updates on updatedPlat are not directly saved in db
        em.detach(updatedPlat);
        updatedPlat
            .nomPlat(UPDATED_NOM_PLAT)
            .imagePath(UPDATED_IMAGE_PATH)
            .prix(UPDATED_PRIX)
            .remisePerc(UPDATED_REMISE_PERC)
            .remiceVal(UPDATED_REMICE_VAL);
        PlatDTO platDTO = platMapper.toDto(updatedPlat);

        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, platDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isOk());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getNomPlat()).isEqualTo(UPDATED_NOM_PLAT);
        assertThat(testPlat.getImagePath()).isEqualTo(UPDATED_IMAGE_PATH);
        assertThat(testPlat.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testPlat.getRemisePerc()).isEqualTo(UPDATED_REMISE_PERC);
        assertThat(testPlat.getRemiceVal()).isEqualTo(UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void putNonExistingPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, platDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlatWithPatch() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeUpdate = platRepository.findAll().size();

        // Update the plat using partial update
        Plat partialUpdatedPlat = new Plat();
        partialUpdatedPlat.setId(plat.getId());

        partialUpdatedPlat
            .nomPlat(UPDATED_NOM_PLAT)
            .imagePath(UPDATED_IMAGE_PATH)
            .prix(UPDATED_PRIX)
            .remisePerc(UPDATED_REMISE_PERC)
            .remiceVal(UPDATED_REMICE_VAL);

        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlat))
            )
            .andExpect(status().isOk());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getNomPlat()).isEqualTo(UPDATED_NOM_PLAT);
        assertThat(testPlat.getImagePath()).isEqualTo(UPDATED_IMAGE_PATH);
        assertThat(testPlat.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testPlat.getRemisePerc()).isEqualTo(UPDATED_REMISE_PERC);
        assertThat(testPlat.getRemiceVal()).isEqualTo(UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void fullUpdatePlatWithPatch() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeUpdate = platRepository.findAll().size();

        // Update the plat using partial update
        Plat partialUpdatedPlat = new Plat();
        partialUpdatedPlat.setId(plat.getId());

        partialUpdatedPlat
            .nomPlat(UPDATED_NOM_PLAT)
            .imagePath(UPDATED_IMAGE_PATH)
            .prix(UPDATED_PRIX)
            .remisePerc(UPDATED_REMISE_PERC)
            .remiceVal(UPDATED_REMICE_VAL);

        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlat))
            )
            .andExpect(status().isOk());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
        Plat testPlat = platList.get(platList.size() - 1);
        assertThat(testPlat.getNomPlat()).isEqualTo(UPDATED_NOM_PLAT);
        assertThat(testPlat.getImagePath()).isEqualTo(UPDATED_IMAGE_PATH);
        assertThat(testPlat.getPrix()).isEqualTo(UPDATED_PRIX);
        assertThat(testPlat.getRemisePerc()).isEqualTo(UPDATED_REMISE_PERC);
        assertThat(testPlat.getRemiceVal()).isEqualTo(UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void patchNonExistingPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, platDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlat() throws Exception {
        int databaseSizeBeforeUpdate = platRepository.findAll().size();
        plat.setId(count.incrementAndGet());

        // Create the Plat
        PlatDTO platDTO = platMapper.toDto(plat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlatMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(platDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Plat in the database
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlat() throws Exception {
        // Initialize the database
        platRepository.saveAndFlush(plat);

        int databaseSizeBeforeDelete = platRepository.findAll().size();

        // Delete the plat
        restPlatMockMvc
            .perform(delete(ENTITY_API_URL_ID, plat.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Plat> platList = platRepository.findAll();
        assertThat(platList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
