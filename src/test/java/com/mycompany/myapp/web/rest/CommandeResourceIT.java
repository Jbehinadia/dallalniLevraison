package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Commande;
import com.mycompany.myapp.repository.CommandeRepository;
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

    private static final Double DEFAULT_REMISE_PERC = 1D;
    private static final Double UPDATED_REMISE_PERC = 2D;

    private static final Double DEFAULT_REMICE_VAL = 1D;
    private static final Double UPDATED_REMICE_VAL = 2D;

    private static final Double DEFAULT_PRIX_LIVRESON = 1D;
    private static final Double UPDATED_PRIX_LIVRESON = 2D;

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
