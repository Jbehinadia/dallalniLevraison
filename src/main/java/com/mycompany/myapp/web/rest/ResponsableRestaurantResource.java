package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ResponsableRestaurantRepository;
import com.mycompany.myapp.service.ResponsableRestaurantQueryService;
import com.mycompany.myapp.service.ResponsableRestaurantService;
import com.mycompany.myapp.service.criteria.ResponsableRestaurantCriteria;
import com.mycompany.myapp.service.dto.ResponsableRestaurantDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.ResponsableRestaurant}.
 */
@RestController
@RequestMapping("/api")
public class ResponsableRestaurantResource {

    private final Logger log = LoggerFactory.getLogger(ResponsableRestaurantResource.class);

    private static final String ENTITY_NAME = "responsableRestaurant";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ResponsableRestaurantService responsableRestaurantService;

    private final ResponsableRestaurantRepository responsableRestaurantRepository;

    private final ResponsableRestaurantQueryService responsableRestaurantQueryService;

    public ResponsableRestaurantResource(
        ResponsableRestaurantService responsableRestaurantService,
        ResponsableRestaurantRepository responsableRestaurantRepository,
        ResponsableRestaurantQueryService responsableRestaurantQueryService
    ) {
        this.responsableRestaurantService = responsableRestaurantService;
        this.responsableRestaurantRepository = responsableRestaurantRepository;
        this.responsableRestaurantQueryService = responsableRestaurantQueryService;
    }

    /**
     * {@code POST  /responsable-restaurants} : Create a new responsableRestaurant.
     *
     * @param responsableRestaurantDTO the responsableRestaurantDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new responsableRestaurantDTO, or with status {@code 400 (Bad Request)} if the responsableRestaurant has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/responsable-restaurants")
    public ResponseEntity<ResponsableRestaurantDTO> createResponsableRestaurant(
        @RequestBody ResponsableRestaurantDTO responsableRestaurantDTO
    ) throws URISyntaxException {
        log.debug("REST request to save ResponsableRestaurant : {}", responsableRestaurantDTO);
        if (responsableRestaurantDTO.getId() != null) {
            throw new BadRequestAlertException("Un nouveau responsableRestaurant ne peut pas déjà avoir un ID", ENTITY_NAME, "idexists");
        }
        ResponsableRestaurantDTO result = responsableRestaurantService.save(responsableRestaurantDTO);
        return ResponseEntity
            .created(new URI("/api/responsable-restaurants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /responsable-restaurants/:id} : Updates an existing responsableRestaurant.
     *
     * @param id the id of the responsableRestaurantDTO to save.
     * @param responsableRestaurantDTO the responsableRestaurantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated responsableRestaurantDTO,
     * or with status {@code 400 (Bad Request)} if the responsableRestaurantDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the responsableRestaurantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/responsable-restaurants/{id}")
    public ResponseEntity<ResponsableRestaurantDTO> updateResponsableRestaurant(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResponsableRestaurantDTO responsableRestaurantDTO
    ) throws URISyntaxException {
        log.debug("REST request to update ResponsableRestaurant : {}, {}", id, responsableRestaurantDTO);
        if (responsableRestaurantDTO.getId() == null) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID nul");
        }
        if (!Objects.equals(id, responsableRestaurantDTO.getId())) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID non valide");
        }

        if (!responsableRestaurantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entité non trouvée", ENTITY_NAME, "ID est introuvable");
        }

        ResponsableRestaurantDTO result = responsableRestaurantService.save(responsableRestaurantDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, responsableRestaurantDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /responsable-restaurants/:id} : Partial updates given fields of an existing responsableRestaurant, field will ignore if it is null
     *
     * @param id the id of the responsableRestaurantDTO to save.
     * @param responsableRestaurantDTO the responsableRestaurantDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated responsableRestaurantDTO,
     * or with status {@code 400 (Bad Request)} if the responsableRestaurantDTO is not valid,
     * or with status {@code 404 (Not Found)} if the responsableRestaurantDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the responsableRestaurantDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/responsable-restaurants/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ResponsableRestaurantDTO> partialUpdateResponsableRestaurant(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ResponsableRestaurantDTO responsableRestaurantDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update ResponsableRestaurant partially : {}, {}", id, responsableRestaurantDTO);
        if (responsableRestaurantDTO.getId() == null) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID nul");
        }
        if (!Objects.equals(id, responsableRestaurantDTO.getId())) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID non valide");
        }

        if (!responsableRestaurantRepository.existsById(id)) {
            throw new BadRequestAlertException("Entité non trouvée", ENTITY_NAME, "ID est introuvable");
        }

        Optional<ResponsableRestaurantDTO> result = responsableRestaurantService.partialUpdate(responsableRestaurantDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, responsableRestaurantDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /responsable-restaurants} : get all the responsableRestaurants.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of responsableRestaurants in body.
     */
    @GetMapping("/responsable-restaurants")
    public ResponseEntity<List<ResponsableRestaurantDTO>> getAllResponsableRestaurants(
        ResponsableRestaurantCriteria criteria,
        Pageable pageable
    ) {
        log.debug("REST request to get ResponsableRestaurants by criteria: {}", criteria);
        Page<ResponsableRestaurantDTO> page = responsableRestaurantQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /responsable-restaurants/count} : count all the responsableRestaurants.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/responsable-restaurants/count")
    public ResponseEntity<Long> countResponsableRestaurants(ResponsableRestaurantCriteria criteria) {
        log.debug("REST request to count ResponsableRestaurants by criteria: {}", criteria);
        return ResponseEntity.ok().body(responsableRestaurantQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /responsable-restaurants/:id} : get the "id" responsableRestaurant.
     *
     * @param id the id of the responsableRestaurantDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the responsableRestaurantDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/responsable-restaurants/{id}")
    public ResponseEntity<ResponsableRestaurantDTO> getResponsableRestaurant(@PathVariable Long id) {
        log.debug("REST request to get ResponsableRestaurant : {}", id);
        Optional<ResponsableRestaurantDTO> responsableRestaurantDTO = responsableRestaurantService.findOne(id);
        return ResponseUtil.wrapOrNotFound(responsableRestaurantDTO);
    }

    /**
     * {@code DELETE  /responsable-restaurants/:id} : delete the "id" responsableRestaurant.
     *
     * @param id the id of the responsableRestaurantDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/responsable-restaurants/{id}")
    public ResponseEntity<Void> deleteResponsableRestaurant(@PathVariable Long id) {
        log.debug("REST request to delete ResponsableRestaurant : {}", id);
        responsableRestaurantService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
