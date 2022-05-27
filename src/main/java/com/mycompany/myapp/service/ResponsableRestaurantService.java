package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.ResponsableRestaurantDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.ResponsableRestaurant}.
 */
public interface ResponsableRestaurantService {
    /**
     * Save a responsableRestaurant.
     *
     * @param responsableRestaurantDTO the entity to save.
     * @return the persisted entity.
     */
    ResponsableRestaurantDTO save(ResponsableRestaurantDTO responsableRestaurantDTO);

    /**
     * Partially updates a responsableRestaurant.
     *
     * @param responsableRestaurantDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ResponsableRestaurantDTO> partialUpdate(ResponsableRestaurantDTO responsableRestaurantDTO);

    /**
     * Get all the responsableRestaurants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<ResponsableRestaurantDTO> findAll(Pageable pageable);

    /**
     * Get the "id" responsableRestaurant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ResponsableRestaurantDTO> findOne(Long id);

    /**
     * Delete the "id" responsableRestaurant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
