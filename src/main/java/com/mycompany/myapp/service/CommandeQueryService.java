package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Commande;
import com.mycompany.myapp.repository.CommandeRepository;
import com.mycompany.myapp.service.criteria.CommandeCriteria;
import com.mycompany.myapp.service.dto.CommandeDTO;
import com.mycompany.myapp.service.mapper.CommandeMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Commande} entities in the database.
 * The main input is a {@link CommandeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CommandeDTO} or a {@link Page} of {@link CommandeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CommandeQueryService extends QueryService<Commande> {

    private final Logger log = LoggerFactory.getLogger(CommandeQueryService.class);

    private final CommandeRepository commandeRepository;

    private final CommandeMapper commandeMapper;

    public CommandeQueryService(CommandeRepository commandeRepository, CommandeMapper commandeMapper) {
        this.commandeRepository = commandeRepository;
        this.commandeMapper = commandeMapper;
    }

    /**
     * Return a {@link List} of {@link CommandeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CommandeDTO> findByCriteria(CommandeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Commande> specification = createSpecification(criteria);
        return commandeMapper.toDto(commandeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CommandeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommandeDTO> findByCriteria(CommandeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Commande> specification = createSpecification(criteria);
        return commandeRepository.findAll(specification, page).map(commandeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommandeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Commande> specification = createSpecification(criteria);
        return commandeRepository.count(specification);
    }

    /**
     * Function to convert {@link CommandeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Commande> createSpecification(CommandeCriteria criteria) {
        Specification<Commande> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Commande_.id));
            }
            if (criteria.getAdresseCommande() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAdresseCommande(), Commande_.adresseCommande));
            }
            if (criteria.getEtat() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEtat(), Commande_.etat));
            }
            if (criteria.getDateCommande() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateCommande(), Commande_.dateCommande));
            }
            if (criteria.getPrixTotal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrixTotal(), Commande_.prixTotal));
            }
            if (criteria.getRemisePerc() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRemisePerc(), Commande_.remisePerc));
            }
            if (criteria.getRemiceVal() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRemiceVal(), Commande_.remiceVal));
            }
            if (criteria.getPrixLivreson() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrixLivreson(), Commande_.prixLivreson));
            }
            if (criteria.getDateSortie() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateSortie(), Commande_.dateSortie));
            }
            if (criteria.getLivreurId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getLivreurId(), root -> root.join(Commande_.livreur, JoinType.LEFT).get(Livreur_.id))
                    );
            }
            if (criteria.getClientId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getClientId(), root -> root.join(Commande_.client, JoinType.LEFT).get(Client_.id))
                    );
            }
        }
        return specification;
    }
}
