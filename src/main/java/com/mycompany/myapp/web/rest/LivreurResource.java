package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.LivreurRepository;
import com.mycompany.myapp.service.LivreurQueryService;
import com.mycompany.myapp.service.LivreurService;
import com.mycompany.myapp.service.criteria.LivreurCriteria;
import com.mycompany.myapp.service.dto.LivreurDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Livreur}.
 */
@RestController
@RequestMapping("/api")
public class LivreurResource {

    private final Logger log = LoggerFactory.getLogger(LivreurResource.class);

    private static final String ENTITY_NAME = "livreur";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LivreurService livreurService;

    private final LivreurRepository livreurRepository;

    private final LivreurQueryService livreurQueryService;

    public LivreurResource(LivreurService livreurService, LivreurRepository livreurRepository, LivreurQueryService livreurQueryService) {
        this.livreurService = livreurService;
        this.livreurRepository = livreurRepository;
        this.livreurQueryService = livreurQueryService;
    }

    /**
     * {@code POST  /livreurs} : Create a new livreur.
     *
     * @param livreurDTO the livreurDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new livreurDTO, or with status {@code 400 (Bad Request)} if the livreur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/livreurs")
    public ResponseEntity<LivreurDTO> createLivreur(@RequestBody LivreurDTO livreurDTO) throws URISyntaxException {
        log.debug("REST request to save Livreur : {}", livreurDTO);
        if (livreurDTO.getId() != null) {
            throw new BadRequestAlertException("A new livreur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LivreurDTO result = livreurService.save(livreurDTO);
        return ResponseEntity
            .created(new URI("/api/livreurs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /livreurs/:id} : Updates an existing livreur.
     *
     * @param id the id of the livreurDTO to save.
     * @param livreurDTO the livreurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livreurDTO,
     * or with status {@code 400 (Bad Request)} if the livreurDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the livreurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/livreurs/{id}")
    public ResponseEntity<LivreurDTO> updateLivreur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LivreurDTO livreurDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Livreur : {}, {}", id, livreurDTO);
        if (livreurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livreurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!livreurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        LivreurDTO result = livreurService.save(livreurDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, livreurDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /livreurs/:id} : Partial updates given fields of an existing livreur, field will ignore if it is null
     *
     * @param id the id of the livreurDTO to save.
     * @param livreurDTO the livreurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livreurDTO,
     * or with status {@code 400 (Bad Request)} if the livreurDTO is not valid,
     * or with status {@code 404 (Not Found)} if the livreurDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the livreurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/livreurs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LivreurDTO> partialUpdateLivreur(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody LivreurDTO livreurDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Livreur partially : {}, {}", id, livreurDTO);
        if (livreurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livreurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!livreurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LivreurDTO> result = livreurService.partialUpdate(livreurDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, livreurDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /livreurs} : get all the livreurs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of livreurs in body.
     */
    @GetMapping("/livreurs")
    public ResponseEntity<List<LivreurDTO>> getAllLivreurs(LivreurCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Livreurs by criteria: {}", criteria);
        Page<LivreurDTO> page = livreurQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /livreurs/count} : count all the livreurs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/livreurs/count")
    public ResponseEntity<Long> countLivreurs(LivreurCriteria criteria) {
        log.debug("REST request to count Livreurs by criteria: {}", criteria);
        return ResponseEntity.ok().body(livreurQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /livreurs/:id} : get the "id" livreur.
     *
     * @param id the id of the livreurDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the livreurDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/livreurs/{id}")
    public ResponseEntity<LivreurDTO> getLivreur(@PathVariable Long id) {
        log.debug("REST request to get Livreur : {}", id);
        Optional<LivreurDTO> livreurDTO = livreurService.findOne(id);
        return ResponseUtil.wrapOrNotFound(livreurDTO);
    }

    /**
     * {@code DELETE  /livreurs/:id} : delete the "id" livreur.
     *
     * @param id the id of the livreurDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/livreurs/{id}")
    public ResponseEntity<Void> deleteLivreur(@PathVariable Long id) {
        log.debug("REST request to delete Livreur : {}", id);
        livreurService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
