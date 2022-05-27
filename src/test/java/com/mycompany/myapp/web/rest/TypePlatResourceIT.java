package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TypePlat;
import com.mycompany.myapp.repository.TypePlatRepository;
import com.mycompany.myapp.service.dto.TypePlatDTO;
import com.mycompany.myapp.service.mapper.TypePlatMapper;
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
 * Integration tests for the {@link TypePlatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypePlatResourceIT {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_PATH = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_PATH = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/type-plats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TypePlatRepository typePlatRepository;

    @Autowired
    private TypePlatMapper typePlatMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTypePlatMockMvc;

    private TypePlat typePlat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypePlat createEntity(EntityManager em) {
        TypePlat typePlat = new TypePlat().type(DEFAULT_TYPE).imagePath(DEFAULT_IMAGE_PATH);
        return typePlat;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypePlat createUpdatedEntity(EntityManager em) {
        TypePlat typePlat = new TypePlat().type(UPDATED_TYPE).imagePath(UPDATED_IMAGE_PATH);
        return typePlat;
    }

    @BeforeEach
    public void initTest() {
        typePlat = createEntity(em);
    }

    @Test
    @Transactional
    void createTypePlat() throws Exception {
        int databaseSizeBeforeCreate = typePlatRepository.findAll().size();
        // Create the TypePlat
        TypePlatDTO typePlatDTO = typePlatMapper.toDto(typePlat);
        restTypePlatMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlatDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeCreate + 1);
        TypePlat testTypePlat = typePlatList.get(typePlatList.size() - 1);
        assertThat(testTypePlat.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTypePlat.getImagePath()).isEqualTo(DEFAULT_IMAGE_PATH);
    }

    @Test
    @Transactional
    void createTypePlatWithExistingId() throws Exception {
        // Create the TypePlat with an existing ID
        typePlat.setId(1L);
        TypePlatDTO typePlatDTO = typePlatMapper.toDto(typePlat);

        int databaseSizeBeforeCreate = typePlatRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypePlatMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTypePlats() throws Exception {
        // Initialize the database
        typePlatRepository.saveAndFlush(typePlat);

        // Get all the typePlatList
        restTypePlatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typePlat.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].imagePath").value(hasItem(DEFAULT_IMAGE_PATH.toString())));
    }

    @Test
    @Transactional
    void getTypePlat() throws Exception {
        // Initialize the database
        typePlatRepository.saveAndFlush(typePlat);

        // Get the typePlat
        restTypePlatMockMvc
            .perform(get(ENTITY_API_URL_ID, typePlat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(typePlat.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.imagePath").value(DEFAULT_IMAGE_PATH.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTypePlat() throws Exception {
        // Get the typePlat
        restTypePlatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTypePlat() throws Exception {
        // Initialize the database
        typePlatRepository.saveAndFlush(typePlat);

        int databaseSizeBeforeUpdate = typePlatRepository.findAll().size();

        // Update the typePlat
        TypePlat updatedTypePlat = typePlatRepository.findById(typePlat.getId()).get();
        // Disconnect from session so that the updates on updatedTypePlat are not directly saved in db
        em.detach(updatedTypePlat);
        updatedTypePlat.type(UPDATED_TYPE).imagePath(UPDATED_IMAGE_PATH);
        TypePlatDTO typePlatDTO = typePlatMapper.toDto(updatedTypePlat);

        restTypePlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typePlatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlatDTO))
            )
            .andExpect(status().isOk());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeUpdate);
        TypePlat testTypePlat = typePlatList.get(typePlatList.size() - 1);
        assertThat(testTypePlat.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTypePlat.getImagePath()).isEqualTo(UPDATED_IMAGE_PATH);
    }

    @Test
    @Transactional
    void putNonExistingTypePlat() throws Exception {
        int databaseSizeBeforeUpdate = typePlatRepository.findAll().size();
        typePlat.setId(count.incrementAndGet());

        // Create the TypePlat
        TypePlatDTO typePlatDTO = typePlatMapper.toDto(typePlat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypePlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typePlatDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTypePlat() throws Exception {
        int databaseSizeBeforeUpdate = typePlatRepository.findAll().size();
        typePlat.setId(count.incrementAndGet());

        // Create the TypePlat
        TypePlatDTO typePlatDTO = typePlatMapper.toDto(typePlat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypePlatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTypePlat() throws Exception {
        int databaseSizeBeforeUpdate = typePlatRepository.findAll().size();
        typePlat.setId(count.incrementAndGet());

        // Create the TypePlat
        TypePlatDTO typePlatDTO = typePlatMapper.toDto(typePlat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypePlatMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(typePlatDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTypePlatWithPatch() throws Exception {
        // Initialize the database
        typePlatRepository.saveAndFlush(typePlat);

        int databaseSizeBeforeUpdate = typePlatRepository.findAll().size();

        // Update the typePlat using partial update
        TypePlat partialUpdatedTypePlat = new TypePlat();
        partialUpdatedTypePlat.setId(typePlat.getId());

        partialUpdatedTypePlat.type(UPDATED_TYPE).imagePath(UPDATED_IMAGE_PATH);

        restTypePlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypePlat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypePlat))
            )
            .andExpect(status().isOk());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeUpdate);
        TypePlat testTypePlat = typePlatList.get(typePlatList.size() - 1);
        assertThat(testTypePlat.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTypePlat.getImagePath()).isEqualTo(UPDATED_IMAGE_PATH);
    }

    @Test
    @Transactional
    void fullUpdateTypePlatWithPatch() throws Exception {
        // Initialize the database
        typePlatRepository.saveAndFlush(typePlat);

        int databaseSizeBeforeUpdate = typePlatRepository.findAll().size();

        // Update the typePlat using partial update
        TypePlat partialUpdatedTypePlat = new TypePlat();
        partialUpdatedTypePlat.setId(typePlat.getId());

        partialUpdatedTypePlat.type(UPDATED_TYPE).imagePath(UPDATED_IMAGE_PATH);

        restTypePlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypePlat.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTypePlat))
            )
            .andExpect(status().isOk());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeUpdate);
        TypePlat testTypePlat = typePlatList.get(typePlatList.size() - 1);
        assertThat(testTypePlat.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTypePlat.getImagePath()).isEqualTo(UPDATED_IMAGE_PATH);
    }

    @Test
    @Transactional
    void patchNonExistingTypePlat() throws Exception {
        int databaseSizeBeforeUpdate = typePlatRepository.findAll().size();
        typePlat.setId(count.incrementAndGet());

        // Create the TypePlat
        TypePlatDTO typePlatDTO = typePlatMapper.toDto(typePlat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypePlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, typePlatDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typePlatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTypePlat() throws Exception {
        int databaseSizeBeforeUpdate = typePlatRepository.findAll().size();
        typePlat.setId(count.incrementAndGet());

        // Create the TypePlat
        TypePlatDTO typePlatDTO = typePlatMapper.toDto(typePlat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypePlatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typePlatDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTypePlat() throws Exception {
        int databaseSizeBeforeUpdate = typePlatRepository.findAll().size();
        typePlat.setId(count.incrementAndGet());

        // Create the TypePlat
        TypePlatDTO typePlatDTO = typePlatMapper.toDto(typePlat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypePlatMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(typePlatDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypePlat in the database
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTypePlat() throws Exception {
        // Initialize the database
        typePlatRepository.saveAndFlush(typePlat);

        int databaseSizeBeforeDelete = typePlatRepository.findAll().size();

        // Delete the typePlat
        restTypePlatMockMvc
            .perform(delete(ENTITY_API_URL_ID, typePlat.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TypePlat> typePlatList = typePlatRepository.findAll();
        assertThat(typePlatList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
