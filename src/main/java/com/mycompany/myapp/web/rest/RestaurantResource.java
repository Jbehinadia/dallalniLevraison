package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.RestaurantRepository;
import com.mycompany.myapp.service.RestaurantQueryService;
import com.mycompany.myapp.service.RestaurantService;
import com.mycompany.myapp.service.criteria.RestaurantCriteria;
import com.mycompany.myapp.service.dto.RestaurantDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Restaurant}.
 */
@RestController
@RequestMapping("/api")
public class RestaurantResource {

    private final Logger log = LoggerFactory.getLogger(RestaurantResource.class);

    private static final String ENTITY_NAME = "restaurant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestaurantService restaurantService;

    private final RestaurantRepository restaurantRepository;

    private final RestaurantQueryService restaurantQueryService;

    public RestaurantResource(
        RestaurantService restaurantService,
        RestaurantRepository restaurantRepository,
        RestaurantQueryService restaurantQueryService
    ) {
        this.restaurantService = restaurantService;
        this.restaurantRepository = restaurantRepository;
        this.restaurantQueryService = restaurantQueryService;
    }

    /**
     * {@code POST  /restaurants} : Create a new restaurant.
     *
     * @param restaurantDTO the restaurantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurantDTO, or with status {@code 400 (Bad Request)} if the restaurant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO restaurantDTO) throws URISyntaxException {
        log.debug("REST request to save Restaurant : {}", restaurantDTO);
        if (restaurantDTO.getId() != null) {
            throw new BadRequestAlertException("Un nouveau restaurant ne peut pas déjà avoir un ID", ENTITY_NAME, "idexists");
        }
        RestaurantDTO result = restaurantService.save(restaurantDTO);
        return ResponseEntity
            .created(new URI("/api/restaurants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /restaurants/:id} : Updates an existing restaurant.
     *
     * @param id the id of the restaurantDTO to save.
     * @param restaurantDTO the restaurantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restaurantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RestaurantDTO restaurantDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Restaurant : {}, {}", id, restaurantDTO);
        if (restaurantDTO.getId() == null) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID nul");
        }
        if (!Objects.equals(id, restaurantDTO.getId())) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID non valide");
        }

        if (!restaurantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entité non trouvée", ENTITY_NAME, "ID est introuvable");
        }

        RestaurantDTO result = restaurantService.save(restaurantDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, restaurantDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /restaurants/:id} : Partial updates given fields of an existing restaurant, field will ignore if it is null
     *
     * @param id the id of the restaurantDTO to save.
     * @param restaurantDTO the restaurantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the restaurantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the restaurantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/restaurants/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RestaurantDTO> partialUpdateRestaurant(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RestaurantDTO restaurantDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Restaurant partially : {}, {}", id, restaurantDTO);
        if (restaurantDTO.getId() == null) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID nul");
        }
        if (!Objects.equals(id, restaurantDTO.getId())) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID non valide");
        }

        if (!restaurantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entité non trouvée", ENTITY_NAME, "ID est introuvable");
        }

        Optional<RestaurantDTO> result = restaurantService.partialUpdate(restaurantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, restaurantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /restaurants} : get all the restaurants.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurants in body.
     */
    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants(RestaurantCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Restaurants by criteria: {}", criteria);
        Page<RestaurantDTO> page = restaurantQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /restaurants/count} : count all the restaurants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/restaurants/count")
    public ResponseEntity<Long> countRestaurants(RestaurantCriteria criteria) {
        log.debug("REST request to count Restaurants by criteria: {}", criteria);
        return ResponseEntity.ok().body(restaurantQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /restaurants/:id} : get the "id" restaurant.
     *
     * @param id the id of the restaurantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/restaurants/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable Long id) {
        log.debug("REST request to get Restaurant : {}", id);
        Optional<RestaurantDTO> restaurantDTO = restaurantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(restaurantDTO);
    }

    /**
     * {@code DELETE  /restaurants/:id} : delete the "id" restaurant.
     *
     * @param id the id of the restaurantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        log.debug("REST request to delete Restaurant : {}", id);
        restaurantService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
