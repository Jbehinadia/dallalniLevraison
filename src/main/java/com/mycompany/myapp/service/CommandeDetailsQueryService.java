package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.CommandeDetails;
import com.mycompany.myapp.repository.CommandeDetailsRepository;
import com.mycompany.myapp.service.criteria.CommandeDetailsCriteria;
import com.mycompany.myapp.service.dto.CommandeDetailsDTO;
import com.mycompany.myapp.service.mapper.CommandeDetailsMapper;
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
 * Service for executing complex queries for {@link CommandeDetails} entities in the database.
 * The main input is a {@link CommandeDetailsCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CommandeDetailsDTO} or a {@link Page} of {@link CommandeDetailsDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CommandeDetailsQueryService extends QueryService<CommandeDetails> {

    private final Logger log = LoggerFactory.getLogger(CommandeDetailsQueryService.class);

    private final CommandeDetailsRepository commandeDetailsRepository;

    private final CommandeDetailsMapper commandeDetailsMapper;

    public CommandeDetailsQueryService(CommandeDetailsRepository commandeDetailsRepository, CommandeDetailsMapper commandeDetailsMapper) {
        this.commandeDetailsRepository = commandeDetailsRepository;
        this.commandeDetailsMapper = commandeDetailsMapper;
    }

    /**
     * Return a {@link List} of {@link CommandeDetailsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CommandeDetailsDTO> findByCriteria(CommandeDetailsCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CommandeDetails> specification = createSpecification(criteria);
        return commandeDetailsMapper.toDto(commandeDetailsRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link CommandeDetailsDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CommandeDetailsDTO> findByCriteria(CommandeDetailsCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CommandeDetails> specification = createSpecification(criteria);
        return commandeDetailsRepository.findAll(specification, page).map(commandeDetailsMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CommandeDetailsCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CommandeDetails> specification = createSpecification(criteria);
        return commandeDetailsRepository.count(specification);
    }

    /**
     * Function to convert {@link CommandeDetailsCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CommandeDetails> createSpecification(CommandeDetailsCriteria criteria) {
        Specification<CommandeDetails> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CommandeDetails_.id));
            }
            if (criteria.getPrix() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrix(), CommandeDetails_.prix));
            }
            if (criteria.getEtat() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEtat(), CommandeDetails_.etat));
            }
            if (criteria.getQte() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getQte(), CommandeDetails_.qte));
            }
            if (criteria.getCommandeId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCommandeId(),
                            root -> root.join(CommandeDetails_.commande, JoinType.LEFT).get(Commande_.id)
                        )
                    );
            }
            if (criteria.getPlatId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPlatId(), root -> root.join(CommandeDetails_.plat, JoinType.LEFT).get(Plat_.id))
                    );
            }
        }
        return specification;
    }
}
