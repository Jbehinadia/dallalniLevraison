package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Livreur;
import com.mycompany.myapp.repository.LivreurRepository;
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
