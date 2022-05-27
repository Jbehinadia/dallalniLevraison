package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.LivreurDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Livreur}.
 */
public interface LivreurService {
    /**
     * Save a livreur.
     *
     * @param livreurDTO the entity to save.
     * @return the persisted entity.
     */
    LivreurDTO save(LivreurDTO livreurDTO);

    /**
     * Partially updates a livreur.
     *
     * @param livreurDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<LivreurDTO> partialUpdate(LivreurDTO livreurDTO);

    /**
     * Get all the livreurs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<LivreurDTO> findAll(Pageable pageable);

    /**
     * Get the "id" livreur.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<LivreurDTO> findOne(Long id);

    /**
     * Delete the "id" livreur.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
