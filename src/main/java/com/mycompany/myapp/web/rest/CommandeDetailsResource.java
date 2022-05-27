package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CommandeDetailsRepository;
import com.mycompany.myapp.service.CommandeDetailsQueryService;
import com.mycompany.myapp.service.CommandeDetailsService;
import com.mycompany.myapp.service.criteria.CommandeDetailsCriteria;
import com.mycompany.myapp.service.dto.CommandeDetailsDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.CommandeDetails}.
 */
@RestController
@RequestMapping("/api")
public class CommandeDetailsResource {

    private final Logger log = LoggerFactory.getLogger(CommandeDetailsResource.class);

    private static final String ENTITY_NAME = "commandeDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommandeDetailsService commandeDetailsService;

    private final CommandeDetailsRepository commandeDetailsRepository;

    private final CommandeDetailsQueryService commandeDetailsQueryService;

    public CommandeDetailsResource(
        CommandeDetailsService commandeDetailsService,
        CommandeDetailsRepository commandeDetailsRepository,
        CommandeDetailsQueryService commandeDetailsQueryService
    ) {
        this.commandeDetailsService = commandeDetailsService;
        this.commandeDetailsRepository = commandeDetailsRepository;
        this.commandeDetailsQueryService = commandeDetailsQueryService;
    }

    /**
     * {@code POST  /commande-details} : Create a new commandeDetails.
     *
     * @param commandeDetailsDTO the commandeDetailsDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commandeDetailsDTO, or with status {@code 400 (Bad Request)} if the commandeDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/commande-details")
    public ResponseEntity<CommandeDetailsDTO> createCommandeDetails(@RequestBody CommandeDetailsDTO commandeDetailsDTO)
        throws URISyntaxException {
        log.debug("REST request to save CommandeDetails : {}", commandeDetailsDTO);
        if (commandeDetailsDTO.getId() != null) {
            throw new BadRequestAlertException("A new commandeDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CommandeDetailsDTO result = commandeDetailsService.save(commandeDetailsDTO);
        return ResponseEntity
            .created(new URI("/api/commande-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /commande-details/:id} : Updates an existing commandeDetails.
     *
     * @param id the id of the commandeDetailsDTO to save.
     * @param commandeDetailsDTO the commandeDetailsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandeDetailsDTO,
     * or with status {@code 400 (Bad Request)} if the commandeDetailsDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commandeDetailsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/commande-details/{id}")
    public ResponseEntity<CommandeDetailsDTO> updateCommandeDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CommandeDetailsDTO commandeDetailsDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CommandeDetails : {}, {}", id, commandeDetailsDTO);
        if (commandeDetailsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandeDetailsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandeDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CommandeDetailsDTO result = commandeDetailsService.save(commandeDetailsDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, commandeDetailsDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /commande-details/:id} : Partial updates given fields of an existing commandeDetails, field will ignore if it is null
     *
     * @param id the id of the commandeDetailsDTO to save.
     * @param commandeDetailsDTO the commandeDetailsDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandeDetailsDTO,
     * or with status {@code 400 (Bad Request)} if the commandeDetailsDTO is not valid,
     * or with status {@code 404 (Not Found)} if the commandeDetailsDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the commandeDetailsDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/commande-details/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CommandeDetailsDTO> partialUpdateCommandeDetails(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CommandeDetailsDTO commandeDetailsDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CommandeDetails partially : {}, {}", id, commandeDetailsDTO);
        if (commandeDetailsDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, commandeDetailsDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!commandeDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CommandeDetailsDTO> result = commandeDetailsService.partialUpdate(commandeDetailsDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, commandeDetailsDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /commande-details} : get all the commandeDetails.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commandeDetails in body.
     */
    @GetMapping("/commande-details")
    public ResponseEntity<List<CommandeDetailsDTO>> getAllCommandeDetails(CommandeDetailsCriteria criteria, Pageable pageable) {
        log.debug("REST request to get CommandeDetails by criteria: {}", criteria);
        Page<CommandeDetailsDTO> page = commandeDetailsQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /commande-details/count} : count all the commandeDetails.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/commande-details/count")
    public ResponseEntity<Long> countCommandeDetails(CommandeDetailsCriteria criteria) {
        log.debug("REST request to count CommandeDetails by criteria: {}", criteria);
        return ResponseEntity.ok().body(commandeDetailsQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /commande-details/:id} : get the "id" commandeDetails.
     *
     * @param id the id of the commandeDetailsDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commandeDetailsDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/commande-details/{id}")
    public ResponseEntity<CommandeDetailsDTO> getCommandeDetails(@PathVariable Long id) {
        log.debug("REST request to get CommandeDetails : {}", id);
        Optional<CommandeDetailsDTO> commandeDetailsDTO = commandeDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(commandeDetailsDTO);
    }

    /**
     * {@code DELETE  /commande-details/:id} : delete the "id" commandeDetails.
     *
     * @param id the id of the commandeDetailsDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/commande-details/{id}")
    public ResponseEntity<Void> deleteCommandeDetails(@PathVariable Long id) {
        log.debug("REST request to delete CommandeDetails : {}", id);
        commandeDetailsService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
