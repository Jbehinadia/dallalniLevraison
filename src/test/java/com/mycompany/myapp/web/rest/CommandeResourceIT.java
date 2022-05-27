package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Client;
import com.mycompany.myapp.domain.Commande;
import com.mycompany.myapp.domain.Livreur;
import com.mycompany.myapp.repository.CommandeRepository;
import com.mycompany.myapp.service.criteria.CommandeCriteria;
import com.mycompany.myapp.service.dto.CommandeDTO;
import com.mycompany.myapp.service.mapper.CommandeMapper;
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
 * Integration tests for the {@link CommandeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CommandeResourceIT {

    private static final String DEFAULT_ADRESSE_COMMANDE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE_COMMANDE = "BBBBBBBBBB";

    private static final String DEFAULT_ETAT = "AAAAAAAAAA";
    private static final String UPDATED_ETAT = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_COMMANDE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_COMMANDE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_PRIX_TOTAL = 1D;
    private static final Double UPDATED_PRIX_TOTAL = 2D;
    private static final Double SMALLER_PRIX_TOTAL = 1D - 1D;

    private static final Double DEFAULT_REMISE_PERC = 1D;
    private static final Double UPDATED_REMISE_PERC = 2D;
    private static final Double SMALLER_REMISE_PERC = 1D - 1D;

    private static final Double DEFAULT_REMICE_VAL = 1D;
    private static final Double UPDATED_REMICE_VAL = 2D;
    private static final Double SMALLER_REMICE_VAL = 1D - 1D;

    private static final Double DEFAULT_PRIX_LIVRESON = 1D;
    private static final Double UPDATED_PRIX_LIVRESON = 2D;
    private static final Double SMALLER_PRIX_LIVRESON = 1D - 1D;

    private static final Instant DEFAULT_DATE_SORTIE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_SORTIE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/commandes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private CommandeMapper commandeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCommandeMockMvc;

    private Commande commande;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createEntity(EntityManager em) {
        Commande commande = new Commande()
            .adresseCommande(DEFAULT_ADRESSE_COMMANDE)
            .etat(DEFAULT_ETAT)
            .dateCommande(DEFAULT_DATE_COMMANDE)
            .prixTotal(DEFAULT_PRIX_TOTAL)
            .remisePerc(DEFAULT_REMISE_PERC)
            .remiceVal(DEFAULT_REMICE_VAL)
            .prixLivreson(DEFAULT_PRIX_LIVRESON)
            .dateSortie(DEFAULT_DATE_SORTIE);
        return commande;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Commande createUpdatedEntity(EntityManager em) {
        Commande commande = new Commande()
            .adresseCommande(UPDATED_ADRESSE_COMMANDE)
            .etat(UPDATED_ETAT)
            .dateCommande(UPDATED_DATE_COMMANDE)
            .prixTotal(UPDATED_PRIX_TOTAL)
            .remisePerc(UPDATED_REMISE_PERC)
            .remiceVal(UPDATED_REMICE_VAL)
            .prixLivreson(UPDATED_PRIX_LIVRESON)
            .dateSortie(UPDATED_DATE_SORTIE);
        return commande;
    }

    @BeforeEach
    public void initTest() {
        commande = createEntity(em);
    }

    @Test
    @Transactional
    void createCommande() throws Exception {
        int databaseSizeBeforeCreate = commandeRepository.findAll().size();
        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);
        restCommandeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeCreate + 1);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getAdresseCommande()).isEqualTo(DEFAULT_ADRESSE_COMMANDE);
        assertThat(testCommande.getEtat()).isEqualTo(DEFAULT_ETAT);
        assertThat(testCommande.getDateCommande()).isEqualTo(DEFAULT_DATE_COMMANDE);
        assertThat(testCommande.getPrixTotal()).isEqualTo(DEFAULT_PRIX_TOTAL);
        assertThat(testCommande.getRemisePerc()).isEqualTo(DEFAULT_REMISE_PERC);
        assertThat(testCommande.getRemiceVal()).isEqualTo(DEFAULT_REMICE_VAL);
        assertThat(testCommande.getPrixLivreson()).isEqualTo(DEFAULT_PRIX_LIVRESON);
        assertThat(testCommande.getDateSortie()).isEqualTo(DEFAULT_DATE_SORTIE);
    }

    @Test
    @Transactional
    void createCommandeWithExistingId() throws Exception {
        // Create the Commande with an existing ID
        commande.setId(1L);
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        int databaseSizeBeforeCreate = commandeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCommandeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCommandes() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commande.getId().intValue())))
            .andExpect(jsonPath("$.[*].adresseCommande").value(hasItem(DEFAULT_ADRESSE_COMMANDE)))
            .andExpect(jsonPath("$.[*].etat").value(hasItem(DEFAULT_ETAT)))
            .andExpect(jsonPath("$.[*].dateCommande").value(hasItem(DEFAULT_DATE_COMMANDE.toString())))
            .andExpect(jsonPath("$.[*].prixTotal").value(hasItem(DEFAULT_PRIX_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].remisePerc").value(hasItem(DEFAULT_REMISE_PERC.doubleValue())))
            .andExpect(jsonPath("$.[*].remiceVal").value(hasItem(DEFAULT_REMICE_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].prixLivreson").value(hasItem(DEFAULT_PRIX_LIVRESON.doubleValue())))
            .andExpect(jsonPath("$.[*].dateSortie").value(hasItem(DEFAULT_DATE_SORTIE.toString())));
    }

    @Test
    @Transactional
    void getCommande() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get the commande
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL_ID, commande.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(commande.getId().intValue()))
            .andExpect(jsonPath("$.adresseCommande").value(DEFAULT_ADRESSE_COMMANDE))
            .andExpect(jsonPath("$.etat").value(DEFAULT_ETAT))
            .andExpect(jsonPath("$.dateCommande").value(DEFAULT_DATE_COMMANDE.toString()))
            .andExpect(jsonPath("$.prixTotal").value(DEFAULT_PRIX_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.remisePerc").value(DEFAULT_REMISE_PERC.doubleValue()))
            .andExpect(jsonPath("$.remiceVal").value(DEFAULT_REMICE_VAL.doubleValue()))
            .andExpect(jsonPath("$.prixLivreson").value(DEFAULT_PRIX_LIVRESON.doubleValue()))
            .andExpect(jsonPath("$.dateSortie").value(DEFAULT_DATE_SORTIE.toString()));
    }

    @Test
    @Transactional
    void getCommandesByIdFiltering() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        Long id = commande.getId();

        defaultCommandeShouldBeFound("id.equals=" + id);
        defaultCommandeShouldNotBeFound("id.notEquals=" + id);

        defaultCommandeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCommandeShouldNotBeFound("id.greaterThan=" + id);

        defaultCommandeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCommandeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCommandesByAdresseCommandeIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where adresseCommande equals to DEFAULT_ADRESSE_COMMANDE
        defaultCommandeShouldBeFound("adresseCommande.equals=" + DEFAULT_ADRESSE_COMMANDE);

        // Get all the commandeList where adresseCommande equals to UPDATED_ADRESSE_COMMANDE
        defaultCommandeShouldNotBeFound("adresseCommande.equals=" + UPDATED_ADRESSE_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByAdresseCommandeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where adresseCommande not equals to DEFAULT_ADRESSE_COMMANDE
        defaultCommandeShouldNotBeFound("adresseCommande.notEquals=" + DEFAULT_ADRESSE_COMMANDE);

        // Get all the commandeList where adresseCommande not equals to UPDATED_ADRESSE_COMMANDE
        defaultCommandeShouldBeFound("adresseCommande.notEquals=" + UPDATED_ADRESSE_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByAdresseCommandeIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where adresseCommande in DEFAULT_ADRESSE_COMMANDE or UPDATED_ADRESSE_COMMANDE
        defaultCommandeShouldBeFound("adresseCommande.in=" + DEFAULT_ADRESSE_COMMANDE + "," + UPDATED_ADRESSE_COMMANDE);

        // Get all the commandeList where adresseCommande equals to UPDATED_ADRESSE_COMMANDE
        defaultCommandeShouldNotBeFound("adresseCommande.in=" + UPDATED_ADRESSE_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByAdresseCommandeIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where adresseCommande is not null
        defaultCommandeShouldBeFound("adresseCommande.specified=true");

        // Get all the commandeList where adresseCommande is null
        defaultCommandeShouldNotBeFound("adresseCommande.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByAdresseCommandeContainsSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where adresseCommande contains DEFAULT_ADRESSE_COMMANDE
        defaultCommandeShouldBeFound("adresseCommande.contains=" + DEFAULT_ADRESSE_COMMANDE);

        // Get all the commandeList where adresseCommande contains UPDATED_ADRESSE_COMMANDE
        defaultCommandeShouldNotBeFound("adresseCommande.contains=" + UPDATED_ADRESSE_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByAdresseCommandeNotContainsSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where adresseCommande does not contain DEFAULT_ADRESSE_COMMANDE
        defaultCommandeShouldNotBeFound("adresseCommande.doesNotContain=" + DEFAULT_ADRESSE_COMMANDE);

        // Get all the commandeList where adresseCommande does not contain UPDATED_ADRESSE_COMMANDE
        defaultCommandeShouldBeFound("adresseCommande.doesNotContain=" + UPDATED_ADRESSE_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByEtatIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where etat equals to DEFAULT_ETAT
        defaultCommandeShouldBeFound("etat.equals=" + DEFAULT_ETAT);

        // Get all the commandeList where etat equals to UPDATED_ETAT
        defaultCommandeShouldNotBeFound("etat.equals=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandesByEtatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where etat not equals to DEFAULT_ETAT
        defaultCommandeShouldNotBeFound("etat.notEquals=" + DEFAULT_ETAT);

        // Get all the commandeList where etat not equals to UPDATED_ETAT
        defaultCommandeShouldBeFound("etat.notEquals=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandesByEtatIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where etat in DEFAULT_ETAT or UPDATED_ETAT
        defaultCommandeShouldBeFound("etat.in=" + DEFAULT_ETAT + "," + UPDATED_ETAT);

        // Get all the commandeList where etat equals to UPDATED_ETAT
        defaultCommandeShouldNotBeFound("etat.in=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandesByEtatIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where etat is not null
        defaultCommandeShouldBeFound("etat.specified=true");

        // Get all the commandeList where etat is null
        defaultCommandeShouldNotBeFound("etat.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByEtatContainsSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where etat contains DEFAULT_ETAT
        defaultCommandeShouldBeFound("etat.contains=" + DEFAULT_ETAT);

        // Get all the commandeList where etat contains UPDATED_ETAT
        defaultCommandeShouldNotBeFound("etat.contains=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandesByEtatNotContainsSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where etat does not contain DEFAULT_ETAT
        defaultCommandeShouldNotBeFound("etat.doesNotContain=" + DEFAULT_ETAT);

        // Get all the commandeList where etat does not contain UPDATED_ETAT
        defaultCommandeShouldBeFound("etat.doesNotContain=" + UPDATED_ETAT);
    }

    @Test
    @Transactional
    void getAllCommandesByDateCommandeIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateCommande equals to DEFAULT_DATE_COMMANDE
        defaultCommandeShouldBeFound("dateCommande.equals=" + DEFAULT_DATE_COMMANDE);

        // Get all the commandeList where dateCommande equals to UPDATED_DATE_COMMANDE
        defaultCommandeShouldNotBeFound("dateCommande.equals=" + UPDATED_DATE_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByDateCommandeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateCommande not equals to DEFAULT_DATE_COMMANDE
        defaultCommandeShouldNotBeFound("dateCommande.notEquals=" + DEFAULT_DATE_COMMANDE);

        // Get all the commandeList where dateCommande not equals to UPDATED_DATE_COMMANDE
        defaultCommandeShouldBeFound("dateCommande.notEquals=" + UPDATED_DATE_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByDateCommandeIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateCommande in DEFAULT_DATE_COMMANDE or UPDATED_DATE_COMMANDE
        defaultCommandeShouldBeFound("dateCommande.in=" + DEFAULT_DATE_COMMANDE + "," + UPDATED_DATE_COMMANDE);

        // Get all the commandeList where dateCommande equals to UPDATED_DATE_COMMANDE
        defaultCommandeShouldNotBeFound("dateCommande.in=" + UPDATED_DATE_COMMANDE);
    }

    @Test
    @Transactional
    void getAllCommandesByDateCommandeIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateCommande is not null
        defaultCommandeShouldBeFound("dateCommande.specified=true");

        // Get all the commandeList where dateCommande is null
        defaultCommandeShouldNotBeFound("dateCommande.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByPrixTotalIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixTotal equals to DEFAULT_PRIX_TOTAL
        defaultCommandeShouldBeFound("prixTotal.equals=" + DEFAULT_PRIX_TOTAL);

        // Get all the commandeList where prixTotal equals to UPDATED_PRIX_TOTAL
        defaultCommandeShouldNotBeFound("prixTotal.equals=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixTotalIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixTotal not equals to DEFAULT_PRIX_TOTAL
        defaultCommandeShouldNotBeFound("prixTotal.notEquals=" + DEFAULT_PRIX_TOTAL);

        // Get all the commandeList where prixTotal not equals to UPDATED_PRIX_TOTAL
        defaultCommandeShouldBeFound("prixTotal.notEquals=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixTotalIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixTotal in DEFAULT_PRIX_TOTAL or UPDATED_PRIX_TOTAL
        defaultCommandeShouldBeFound("prixTotal.in=" + DEFAULT_PRIX_TOTAL + "," + UPDATED_PRIX_TOTAL);

        // Get all the commandeList where prixTotal equals to UPDATED_PRIX_TOTAL
        defaultCommandeShouldNotBeFound("prixTotal.in=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixTotalIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixTotal is not null
        defaultCommandeShouldBeFound("prixTotal.specified=true");

        // Get all the commandeList where prixTotal is null
        defaultCommandeShouldNotBeFound("prixTotal.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByPrixTotalIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixTotal is greater than or equal to DEFAULT_PRIX_TOTAL
        defaultCommandeShouldBeFound("prixTotal.greaterThanOrEqual=" + DEFAULT_PRIX_TOTAL);

        // Get all the commandeList where prixTotal is greater than or equal to UPDATED_PRIX_TOTAL
        defaultCommandeShouldNotBeFound("prixTotal.greaterThanOrEqual=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixTotalIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixTotal is less than or equal to DEFAULT_PRIX_TOTAL
        defaultCommandeShouldBeFound("prixTotal.lessThanOrEqual=" + DEFAULT_PRIX_TOTAL);

        // Get all the commandeList where prixTotal is less than or equal to SMALLER_PRIX_TOTAL
        defaultCommandeShouldNotBeFound("prixTotal.lessThanOrEqual=" + SMALLER_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixTotalIsLessThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixTotal is less than DEFAULT_PRIX_TOTAL
        defaultCommandeShouldNotBeFound("prixTotal.lessThan=" + DEFAULT_PRIX_TOTAL);

        // Get all the commandeList where prixTotal is less than UPDATED_PRIX_TOTAL
        defaultCommandeShouldBeFound("prixTotal.lessThan=" + UPDATED_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixTotalIsGreaterThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixTotal is greater than DEFAULT_PRIX_TOTAL
        defaultCommandeShouldNotBeFound("prixTotal.greaterThan=" + DEFAULT_PRIX_TOTAL);

        // Get all the commandeList where prixTotal is greater than SMALLER_PRIX_TOTAL
        defaultCommandeShouldBeFound("prixTotal.greaterThan=" + SMALLER_PRIX_TOTAL);
    }

    @Test
    @Transactional
    void getAllCommandesByRemisePercIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remisePerc equals to DEFAULT_REMISE_PERC
        defaultCommandeShouldBeFound("remisePerc.equals=" + DEFAULT_REMISE_PERC);

        // Get all the commandeList where remisePerc equals to UPDATED_REMISE_PERC
        defaultCommandeShouldNotBeFound("remisePerc.equals=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllCommandesByRemisePercIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remisePerc not equals to DEFAULT_REMISE_PERC
        defaultCommandeShouldNotBeFound("remisePerc.notEquals=" + DEFAULT_REMISE_PERC);

        // Get all the commandeList where remisePerc not equals to UPDATED_REMISE_PERC
        defaultCommandeShouldBeFound("remisePerc.notEquals=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllCommandesByRemisePercIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remisePerc in DEFAULT_REMISE_PERC or UPDATED_REMISE_PERC
        defaultCommandeShouldBeFound("remisePerc.in=" + DEFAULT_REMISE_PERC + "," + UPDATED_REMISE_PERC);

        // Get all the commandeList where remisePerc equals to UPDATED_REMISE_PERC
        defaultCommandeShouldNotBeFound("remisePerc.in=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllCommandesByRemisePercIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remisePerc is not null
        defaultCommandeShouldBeFound("remisePerc.specified=true");

        // Get all the commandeList where remisePerc is null
        defaultCommandeShouldNotBeFound("remisePerc.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByRemisePercIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remisePerc is greater than or equal to DEFAULT_REMISE_PERC
        defaultCommandeShouldBeFound("remisePerc.greaterThanOrEqual=" + DEFAULT_REMISE_PERC);

        // Get all the commandeList where remisePerc is greater than or equal to UPDATED_REMISE_PERC
        defaultCommandeShouldNotBeFound("remisePerc.greaterThanOrEqual=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllCommandesByRemisePercIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remisePerc is less than or equal to DEFAULT_REMISE_PERC
        defaultCommandeShouldBeFound("remisePerc.lessThanOrEqual=" + DEFAULT_REMISE_PERC);

        // Get all the commandeList where remisePerc is less than or equal to SMALLER_REMISE_PERC
        defaultCommandeShouldNotBeFound("remisePerc.lessThanOrEqual=" + SMALLER_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllCommandesByRemisePercIsLessThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remisePerc is less than DEFAULT_REMISE_PERC
        defaultCommandeShouldNotBeFound("remisePerc.lessThan=" + DEFAULT_REMISE_PERC);

        // Get all the commandeList where remisePerc is less than UPDATED_REMISE_PERC
        defaultCommandeShouldBeFound("remisePerc.lessThan=" + UPDATED_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllCommandesByRemisePercIsGreaterThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remisePerc is greater than DEFAULT_REMISE_PERC
        defaultCommandeShouldNotBeFound("remisePerc.greaterThan=" + DEFAULT_REMISE_PERC);

        // Get all the commandeList where remisePerc is greater than SMALLER_REMISE_PERC
        defaultCommandeShouldBeFound("remisePerc.greaterThan=" + SMALLER_REMISE_PERC);
    }

    @Test
    @Transactional
    void getAllCommandesByRemiceValIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remiceVal equals to DEFAULT_REMICE_VAL
        defaultCommandeShouldBeFound("remiceVal.equals=" + DEFAULT_REMICE_VAL);

        // Get all the commandeList where remiceVal equals to UPDATED_REMICE_VAL
        defaultCommandeShouldNotBeFound("remiceVal.equals=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllCommandesByRemiceValIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remiceVal not equals to DEFAULT_REMICE_VAL
        defaultCommandeShouldNotBeFound("remiceVal.notEquals=" + DEFAULT_REMICE_VAL);

        // Get all the commandeList where remiceVal not equals to UPDATED_REMICE_VAL
        defaultCommandeShouldBeFound("remiceVal.notEquals=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllCommandesByRemiceValIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remiceVal in DEFAULT_REMICE_VAL or UPDATED_REMICE_VAL
        defaultCommandeShouldBeFound("remiceVal.in=" + DEFAULT_REMICE_VAL + "," + UPDATED_REMICE_VAL);

        // Get all the commandeList where remiceVal equals to UPDATED_REMICE_VAL
        defaultCommandeShouldNotBeFound("remiceVal.in=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllCommandesByRemiceValIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remiceVal is not null
        defaultCommandeShouldBeFound("remiceVal.specified=true");

        // Get all the commandeList where remiceVal is null
        defaultCommandeShouldNotBeFound("remiceVal.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByRemiceValIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remiceVal is greater than or equal to DEFAULT_REMICE_VAL
        defaultCommandeShouldBeFound("remiceVal.greaterThanOrEqual=" + DEFAULT_REMICE_VAL);

        // Get all the commandeList where remiceVal is greater than or equal to UPDATED_REMICE_VAL
        defaultCommandeShouldNotBeFound("remiceVal.greaterThanOrEqual=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllCommandesByRemiceValIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remiceVal is less than or equal to DEFAULT_REMICE_VAL
        defaultCommandeShouldBeFound("remiceVal.lessThanOrEqual=" + DEFAULT_REMICE_VAL);

        // Get all the commandeList where remiceVal is less than or equal to SMALLER_REMICE_VAL
        defaultCommandeShouldNotBeFound("remiceVal.lessThanOrEqual=" + SMALLER_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllCommandesByRemiceValIsLessThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remiceVal is less than DEFAULT_REMICE_VAL
        defaultCommandeShouldNotBeFound("remiceVal.lessThan=" + DEFAULT_REMICE_VAL);

        // Get all the commandeList where remiceVal is less than UPDATED_REMICE_VAL
        defaultCommandeShouldBeFound("remiceVal.lessThan=" + UPDATED_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllCommandesByRemiceValIsGreaterThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where remiceVal is greater than DEFAULT_REMICE_VAL
        defaultCommandeShouldNotBeFound("remiceVal.greaterThan=" + DEFAULT_REMICE_VAL);

        // Get all the commandeList where remiceVal is greater than SMALLER_REMICE_VAL
        defaultCommandeShouldBeFound("remiceVal.greaterThan=" + SMALLER_REMICE_VAL);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixLivresonIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixLivreson equals to DEFAULT_PRIX_LIVRESON
        defaultCommandeShouldBeFound("prixLivreson.equals=" + DEFAULT_PRIX_LIVRESON);

        // Get all the commandeList where prixLivreson equals to UPDATED_PRIX_LIVRESON
        defaultCommandeShouldNotBeFound("prixLivreson.equals=" + UPDATED_PRIX_LIVRESON);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixLivresonIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixLivreson not equals to DEFAULT_PRIX_LIVRESON
        defaultCommandeShouldNotBeFound("prixLivreson.notEquals=" + DEFAULT_PRIX_LIVRESON);

        // Get all the commandeList where prixLivreson not equals to UPDATED_PRIX_LIVRESON
        defaultCommandeShouldBeFound("prixLivreson.notEquals=" + UPDATED_PRIX_LIVRESON);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixLivresonIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixLivreson in DEFAULT_PRIX_LIVRESON or UPDATED_PRIX_LIVRESON
        defaultCommandeShouldBeFound("prixLivreson.in=" + DEFAULT_PRIX_LIVRESON + "," + UPDATED_PRIX_LIVRESON);

        // Get all the commandeList where prixLivreson equals to UPDATED_PRIX_LIVRESON
        defaultCommandeShouldNotBeFound("prixLivreson.in=" + UPDATED_PRIX_LIVRESON);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixLivresonIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixLivreson is not null
        defaultCommandeShouldBeFound("prixLivreson.specified=true");

        // Get all the commandeList where prixLivreson is null
        defaultCommandeShouldNotBeFound("prixLivreson.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByPrixLivresonIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixLivreson is greater than or equal to DEFAULT_PRIX_LIVRESON
        defaultCommandeShouldBeFound("prixLivreson.greaterThanOrEqual=" + DEFAULT_PRIX_LIVRESON);

        // Get all the commandeList where prixLivreson is greater than or equal to UPDATED_PRIX_LIVRESON
        defaultCommandeShouldNotBeFound("prixLivreson.greaterThanOrEqual=" + UPDATED_PRIX_LIVRESON);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixLivresonIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixLivreson is less than or equal to DEFAULT_PRIX_LIVRESON
        defaultCommandeShouldBeFound("prixLivreson.lessThanOrEqual=" + DEFAULT_PRIX_LIVRESON);

        // Get all the commandeList where prixLivreson is less than or equal to SMALLER_PRIX_LIVRESON
        defaultCommandeShouldNotBeFound("prixLivreson.lessThanOrEqual=" + SMALLER_PRIX_LIVRESON);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixLivresonIsLessThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixLivreson is less than DEFAULT_PRIX_LIVRESON
        defaultCommandeShouldNotBeFound("prixLivreson.lessThan=" + DEFAULT_PRIX_LIVRESON);

        // Get all the commandeList where prixLivreson is less than UPDATED_PRIX_LIVRESON
        defaultCommandeShouldBeFound("prixLivreson.lessThan=" + UPDATED_PRIX_LIVRESON);
    }

    @Test
    @Transactional
    void getAllCommandesByPrixLivresonIsGreaterThanSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where prixLivreson is greater than DEFAULT_PRIX_LIVRESON
        defaultCommandeShouldNotBeFound("prixLivreson.greaterThan=" + DEFAULT_PRIX_LIVRESON);

        // Get all the commandeList where prixLivreson is greater than SMALLER_PRIX_LIVRESON
        defaultCommandeShouldBeFound("prixLivreson.greaterThan=" + SMALLER_PRIX_LIVRESON);
    }

    @Test
    @Transactional
    void getAllCommandesByDateSortieIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateSortie equals to DEFAULT_DATE_SORTIE
        defaultCommandeShouldBeFound("dateSortie.equals=" + DEFAULT_DATE_SORTIE);

        // Get all the commandeList where dateSortie equals to UPDATED_DATE_SORTIE
        defaultCommandeShouldNotBeFound("dateSortie.equals=" + UPDATED_DATE_SORTIE);
    }

    @Test
    @Transactional
    void getAllCommandesByDateSortieIsNotEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateSortie not equals to DEFAULT_DATE_SORTIE
        defaultCommandeShouldNotBeFound("dateSortie.notEquals=" + DEFAULT_DATE_SORTIE);

        // Get all the commandeList where dateSortie not equals to UPDATED_DATE_SORTIE
        defaultCommandeShouldBeFound("dateSortie.notEquals=" + UPDATED_DATE_SORTIE);
    }

    @Test
    @Transactional
    void getAllCommandesByDateSortieIsInShouldWork() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateSortie in DEFAULT_DATE_SORTIE or UPDATED_DATE_SORTIE
        defaultCommandeShouldBeFound("dateSortie.in=" + DEFAULT_DATE_SORTIE + "," + UPDATED_DATE_SORTIE);

        // Get all the commandeList where dateSortie equals to UPDATED_DATE_SORTIE
        defaultCommandeShouldNotBeFound("dateSortie.in=" + UPDATED_DATE_SORTIE);
    }

    @Test
    @Transactional
    void getAllCommandesByDateSortieIsNullOrNotNull() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        // Get all the commandeList where dateSortie is not null
        defaultCommandeShouldBeFound("dateSortie.specified=true");

        // Get all the commandeList where dateSortie is null
        defaultCommandeShouldNotBeFound("dateSortie.specified=false");
    }

    @Test
    @Transactional
    void getAllCommandesByLivreurIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);
        Livreur livreur;
        if (TestUtil.findAll(em, Livreur.class).isEmpty()) {
            livreur = LivreurResourceIT.createEntity(em);
            em.persist(livreur);
            em.flush();
        } else {
            livreur = TestUtil.findAll(em, Livreur.class).get(0);
        }
        em.persist(livreur);
        em.flush();
        commande.setLivreur(livreur);
        commandeRepository.saveAndFlush(commande);
        Long livreurId = livreur.getId();

        // Get all the commandeList where livreur equals to livreurId
        defaultCommandeShouldBeFound("livreurId.equals=" + livreurId);

        // Get all the commandeList where livreur equals to (livreurId + 1)
        defaultCommandeShouldNotBeFound("livreurId.equals=" + (livreurId + 1));
    }

    @Test
    @Transactional
    void getAllCommandesByClientIsEqualToSomething() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);
        Client client;
        if (TestUtil.findAll(em, Client.class).isEmpty()) {
            client = ClientResourceIT.createEntity(em);
            em.persist(client);
            em.flush();
        } else {
            client = TestUtil.findAll(em, Client.class).get(0);
        }
        em.persist(client);
        em.flush();
        commande.setClient(client);
        commandeRepository.saveAndFlush(commande);
        Long clientId = client.getId();

        // Get all the commandeList where client equals to clientId
        defaultCommandeShouldBeFound("clientId.equals=" + clientId);

        // Get all the commandeList where client equals to (clientId + 1)
        defaultCommandeShouldNotBeFound("clientId.equals=" + (clientId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommandeShouldBeFound(String filter) throws Exception {
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(commande.getId().intValue())))
            .andExpect(jsonPath("$.[*].adresseCommande").value(hasItem(DEFAULT_ADRESSE_COMMANDE)))
            .andExpect(jsonPath("$.[*].etat").value(hasItem(DEFAULT_ETAT)))
            .andExpect(jsonPath("$.[*].dateCommande").value(hasItem(DEFAULT_DATE_COMMANDE.toString())))
            .andExpect(jsonPath("$.[*].prixTotal").value(hasItem(DEFAULT_PRIX_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].remisePerc").value(hasItem(DEFAULT_REMISE_PERC.doubleValue())))
            .andExpect(jsonPath("$.[*].remiceVal").value(hasItem(DEFAULT_REMICE_VAL.doubleValue())))
            .andExpect(jsonPath("$.[*].prixLivreson").value(hasItem(DEFAULT_PRIX_LIVRESON.doubleValue())))
            .andExpect(jsonPath("$.[*].dateSortie").value(hasItem(DEFAULT_DATE_SORTIE.toString())));

        // Check, that the count call also returns 1
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCommandeShouldNotBeFound(String filter) throws Exception {
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCommandeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCommande() throws Exception {
        // Get the commande
        restCommandeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCommande() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();

        // Update the commande
        Commande updatedCommande = commandeRepository.findById(commande.getId()).get();
        // Disconnect from session so that the updates on updatedCommande are not directly saved in db
        em.detach(updatedCommande);
        updatedCommande
            .adresseCommande(UPDATED_ADRESSE_COMMANDE)
            .etat(UPDATED_ETAT)
            .dateCommande(UPDATED_DATE_COMMANDE)
            .prixTotal(UPDATED_PRIX_TOTAL)
            .remisePerc(UPDATED_REMISE_PERC)
            .remiceVal(UPDATED_REMICE_VAL)
            .prixLivreson(UPDATED_PRIX_LIVRESON)
            .dateSortie(UPDATED_DATE_SORTIE);
        CommandeDTO commandeDTO = commandeMapper.toDto(updatedCommande);

        restCommandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getAdresseCommande()).isEqualTo(UPDATED_ADRESSE_COMMANDE);
        assertThat(testCommande.getEtat()).isEqualTo(UPDATED_ETAT);
        assertThat(testCommande.getDateCommande()).isEqualTo(UPDATED_DATE_COMMANDE);
        assertThat(testCommande.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testCommande.getRemisePerc()).isEqualTo(UPDATED_REMISE_PERC);
        assertThat(testCommande.getRemiceVal()).isEqualTo(UPDATED_REMICE_VAL);
        assertThat(testCommande.getPrixLivreson()).isEqualTo(UPDATED_PRIX_LIVRESON);
        assertThat(testCommande.getDateSortie()).isEqualTo(UPDATED_DATE_SORTIE);
    }

    @Test
    @Transactional
    void putNonExistingCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();
        commande.setId(count.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, commandeDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();
        commande.setId(count.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();
        commande.setId(count.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(commandeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCommandeWithPatch() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();

        // Update the commande using partial update
        Commande partialUpdatedCommande = new Commande();
        partialUpdatedCommande.setId(commande.getId());

        partialUpdatedCommande
            .adresseCommande(UPDATED_ADRESSE_COMMANDE)
            .prixTotal(UPDATED_PRIX_TOTAL)
            .remisePerc(UPDATED_REMISE_PERC)
            .prixLivreson(UPDATED_PRIX_LIVRESON)
            .dateSortie(UPDATED_DATE_SORTIE);

        restCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommande.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommande))
            )
            .andExpect(status().isOk());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getAdresseCommande()).isEqualTo(UPDATED_ADRESSE_COMMANDE);
        assertThat(testCommande.getEtat()).isEqualTo(DEFAULT_ETAT);
        assertThat(testCommande.getDateCommande()).isEqualTo(DEFAULT_DATE_COMMANDE);
        assertThat(testCommande.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testCommande.getRemisePerc()).isEqualTo(UPDATED_REMISE_PERC);
        assertThat(testCommande.getRemiceVal()).isEqualTo(DEFAULT_REMICE_VAL);
        assertThat(testCommande.getPrixLivreson()).isEqualTo(UPDATED_PRIX_LIVRESON);
        assertThat(testCommande.getDateSortie()).isEqualTo(UPDATED_DATE_SORTIE);
    }

    @Test
    @Transactional
    void fullUpdateCommandeWithPatch() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();

        // Update the commande using partial update
        Commande partialUpdatedCommande = new Commande();
        partialUpdatedCommande.setId(commande.getId());

        partialUpdatedCommande
            .adresseCommande(UPDATED_ADRESSE_COMMANDE)
            .etat(UPDATED_ETAT)
            .dateCommande(UPDATED_DATE_COMMANDE)
            .prixTotal(UPDATED_PRIX_TOTAL)
            .remisePerc(UPDATED_REMISE_PERC)
            .remiceVal(UPDATED_REMICE_VAL)
            .prixLivreson(UPDATED_PRIX_LIVRESON)
            .dateSortie(UPDATED_DATE_SORTIE);

        restCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCommande.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCommande))
            )
            .andExpect(status().isOk());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
        Commande testCommande = commandeList.get(commandeList.size() - 1);
        assertThat(testCommande.getAdresseCommande()).isEqualTo(UPDATED_ADRESSE_COMMANDE);
        assertThat(testCommande.getEtat()).isEqualTo(UPDATED_ETAT);
        assertThat(testCommande.getDateCommande()).isEqualTo(UPDATED_DATE_COMMANDE);
        assertThat(testCommande.getPrixTotal()).isEqualTo(UPDATED_PRIX_TOTAL);
        assertThat(testCommande.getRemisePerc()).isEqualTo(UPDATED_REMISE_PERC);
        assertThat(testCommande.getRemiceVal()).isEqualTo(UPDATED_REMICE_VAL);
        assertThat(testCommande.getPrixLivreson()).isEqualTo(UPDATED_PRIX_LIVRESON);
        assertThat(testCommande.getDateSortie()).isEqualTo(UPDATED_DATE_SORTIE);
    }

    @Test
    @Transactional
    void patchNonExistingCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();
        commande.setId(count.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, commandeDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();
        commande.setId(count.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCommande() throws Exception {
        int databaseSizeBeforeUpdate = commandeRepository.findAll().size();
        commande.setId(count.incrementAndGet());

        // Create the Commande
        CommandeDTO commandeDTO = commandeMapper.toDto(commande);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCommandeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(commandeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Commande in the database
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCommande() throws Exception {
        // Initialize the database
        commandeRepository.saveAndFlush(commande);

        int databaseSizeBeforeDelete = commandeRepository.findAll().size();

        // Delete the commande
        restCommandeMockMvc
            .perform(delete(ENTITY_API_URL_ID, commande.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Commande> commandeList = commandeRepository.findAll();
        assertThat(commandeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
