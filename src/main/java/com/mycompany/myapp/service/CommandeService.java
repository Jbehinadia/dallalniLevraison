package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.CommandeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Commande}.
 */
public interface CommandeService {
    /**
     * Save a commande.
     *
     * @param commandeDTO the entity to save.
     * @return the persisted entity.
     */
    CommandeDTO save(CommandeDTO commandeDTO);

    /**
     * Partially updates a commande.
     *
     * @param commandeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CommandeDTO> partialUpdate(CommandeDTO commandeDTO);

    /**
     * Get all the commandes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CommandeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" commande.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CommandeDTO> findOne(Long id);

    /**
     * Delete the "id" commande.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
