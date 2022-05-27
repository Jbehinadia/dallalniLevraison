package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.CommandeDetails;
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

    private static final Integer DEFAULT_QTY = 1;
    private static final Integer UPDATED_QTY = 2;

    private static final String DEFAULT_ETAT = "AAAAAAAAAA";
    private static final String UPDATED_ETAT = "BBBBBBBBBB";

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
        CommandeDetails commandeDetails = new CommandeDetails().prix(DEFAULT_PRIX).qty(DEFAULT_QTY).etat(DEFAULT_ETAT);
        return commandeDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CommandeDetails createUpdatedEntity(EntityManager em) {
        CommandeDetails commandeDetails = new CommandeDetails().prix(UPDATED_PRIX).qty(UPDATED_QTY).etat(UPDATED_ETAT);
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
        assertThat(testCommandeDetails.getQty()).isEqualTo(DEFAULT_QTY);
        assertThat(testCommandeDetails.getEtat()).isEqualTo(DEFAULT_ETAT);
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
            .andExpect(jsonPath("$.[*].qty").value(hasItem(DEFAULT_QTY)))
            .andExpect(jsonPath("$.[*].etat").value(hasItem(DEFAULT_ETAT)));
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
            .andExpect(jsonPath("$.qty").value(DEFAULT_QTY))
            .andExpect(jsonPath("$.etat").value(DEFAULT_ETAT));
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
        updatedCommandeDetails.prix(UPDATED_PRIX).qty(UPDATED_QTY).etat(UPDATED_ETAT);
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
        assertThat(testCommandeDetails.getQty()).isEqualTo(UPDATED_QTY);
        assertThat(testCommandeDetails.getEtat()).isEqualTo(UPDATED_ETAT);
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

        partialUpdatedCommandeDetails.prix(UPDATED_PRIX).qty(UPDATED_QTY).etat(UPDATED_ETAT);

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
        assertThat(testCommandeDetails.getQty()).isEqualTo(UPDATED_QTY);
        assertThat(testCommandeDetails.getEtat()).isEqualTo(UPDATED_ETAT);
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

        partialUpdatedCommandeDetails.prix(UPDATED_PRIX).qty(UPDATED_QTY).etat(UPDATED_ETAT);

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
        assertThat(testCommandeDetails.getQty()).isEqualTo(UPDATED_QTY);
        assertThat(testCommandeDetails.getEtat()).isEqualTo(UPDATED_ETAT);
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
