package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.CommandeDetailsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.CommandeDetails}.
 */
public interface CommandeDetailsService {
    /**
     * Save a commandeDetails.
     *
     * @param commandeDetailsDTO the entity to save.
     * @return the persisted entity.
     */
    CommandeDetailsDTO save(CommandeDetailsDTO commandeDetailsDTO);

    /**
     * Partially updates a commandeDetails.
     *
     * @param commandeDetailsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CommandeDetailsDTO> partialUpdate(CommandeDetailsDTO commandeDetailsDTO);

    /**
     * Get all the commandeDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CommandeDetailsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" commandeDetails.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CommandeDetailsDTO> findOne(Long id);

    /**
     * Delete the "id" commandeDetails.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
