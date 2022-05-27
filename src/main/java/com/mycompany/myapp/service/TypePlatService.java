package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.TypePlatDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TypePlat}.
 */
public interface TypePlatService {
    /**
     * Save a typePlat.
     *
     * @param typePlatDTO the entity to save.
     * @return the persisted entity.
     */
    TypePlatDTO save(TypePlatDTO typePlatDTO);

    /**
     * Partially updates a typePlat.
     *
     * @param typePlatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TypePlatDTO> partialUpdate(TypePlatDTO typePlatDTO);

    /**
     * Get all the typePlats.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TypePlatDTO> findAll(Pageable pageable);

    /**
     * Get the "id" typePlat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TypePlatDTO> findOne(Long id);

    /**
     * Delete the "id" typePlat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
