package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CommandeRepository;
import com.mycompany.myapp.service.CommandeQueryService;
import com.mycompany.myapp.service.CommandeService;
import com.mycompany.myapp.service.criteria.CommandeCriteria;
import com.mycompany.myapp.service.dto.CommandeDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Commande}.
 */
@RestController
@RequestMapping("/api")
public class CommandeResource {

    private final Logger log = LoggerFactory.getLogger(CommandeResource.class);

    private static final String ENTITY_NAME = "commande";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CommandeService commandeService;

    private final CommandeRepository commandeRepository;

    private final CommandeQueryService commandeQueryService;

    public CommandeResource(
        CommandeService commandeService,
        CommandeRepository commandeRepository,
        CommandeQueryService commandeQueryService
    ) {
        this.commandeService = commandeService;
        this.commandeRepository = commandeRepository;
        this.commandeQueryService = commandeQueryService;
    }

    /**
     * {@code POST  /commandes} : Create a new commande.
     *
     * @param commandeDTO the commandeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new commandeDTO, or with status {@code 400 (Bad Request)} if the commande has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/commandes")
    public ResponseEntity<CommandeDTO> createCommande(@RequestBody CommandeDTO commandeDTO) throws URISyntaxException {
        log.debug("REST request to save Commande : {}", commandeDTO);
        if (commandeDTO.getId() != null) {
            throw new BadRequestAlertException("Une nouvelle commande ne peut pas déjà avoir d’ID", ENTITY_NAME, "idexists");
        }
        CommandeDTO result = commandeService.save(commandeDTO);
        return ResponseEntity
            .created(new URI("/api/commandes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /commandes/:id} : Updates an existing commande.
     *
     * @param id the id of the commandeDTO to save.
     * @param commandeDTO the commandeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandeDTO,
     * or with status {@code 400 (Bad Request)} if the commandeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the commandeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/commandes/{id}")
    public ResponseEntity<CommandeDTO> updateCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CommandeDTO commandeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Commande : {}, {}", id, commandeDTO);
        if (commandeDTO.getId() == null) {
            throw new BadRequestAlertException("IDnon valide", ENTITY_NAME, "ID nul");
        }
        if (!Objects.equals(id, commandeDTO.getId())) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID non valide");
        }

        if (!commandeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entité non trouvée", ENTITY_NAME, "ID est introuvable");
        }

        CommandeDTO result = commandeService.save(commandeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, commandeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /commandes/:id} : Partial updates given fields of an existing commande, field will ignore if it is null
     *
     * @param id the id of the commandeDTO to save.
     * @param commandeDTO the commandeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated commandeDTO,
     * or with status {@code 400 (Bad Request)} if the commandeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the commandeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the commandeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/commandes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CommandeDTO> partialUpdateCommande(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CommandeDTO commandeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Commande partially : {}, {}", id, commandeDTO);
        if (commandeDTO.getId() == null) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID nul");
        }
        if (!Objects.equals(id, commandeDTO.getId())) {
            throw new BadRequestAlertException("ID non valide", ENTITY_NAME, "ID non valided");
        }

        if (!commandeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entité non trouvée", ENTITY_NAME, "ID est introuvable");
        }

        Optional<CommandeDTO> result = commandeService.partialUpdate(commandeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, commandeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /commandes} : get all the commandes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of commandes in body.
     */
    @GetMapping("/commandes")
    public ResponseEntity<List<CommandeDTO>> getAllCommandes(CommandeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Commandes by criteria: {}", criteria);
        Page<CommandeDTO> page = commandeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /commandes/count} : count all the commandes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/commandes/count")
    public ResponseEntity<Long> countCommandes(CommandeCriteria criteria) {
        log.debug("REST request to count Commandes by criteria: {}", criteria);
        return ResponseEntity.ok().body(commandeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /commandes/:id} : get the "id" commande.
     *
     * @param id the id of the commandeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the commandeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/commandes/{id}")
    public ResponseEntity<CommandeDTO> getCommande(@PathVariable Long id) {
        log.debug("REST request to get Commande : {}", id);
        Optional<CommandeDTO> commandeDTO = commandeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(commandeDTO);
    }

    /**
     * {@code DELETE  /commandes/:id} : delete the "id" commande.
     *
     * @param id the id of the commandeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/commandes/{id}")
    public ResponseEntity<Void> deleteCommande(@PathVariable Long id) {
        log.debug("REST request to delete Commande : {}", id);
        commandeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
