package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.ResponsableRestaurant;
import com.mycompany.myapp.repository.ResponsableRestaurantRepository;
import com.mycompany.myapp.service.criteria.ResponsableRestaurantCriteria;
import com.mycompany.myapp.service.dto.ResponsableRestaurantDTO;
import com.mycompany.myapp.service.mapper.ResponsableRestaurantMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ResponsableRestaurant} entities in the database.
 * The main input is a {@link ResponsableRestaurantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ResponsableRestaurantDTO} or a {@link Page} of {@link ResponsableRestaurantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ResponsableRestaurantQueryService extends QueryService<ResponsableRestaurant> {

    private final Logger log = LoggerFactory.getLogger(ResponsableRestaurantQueryService.class);

    private final ResponsableRestaurantRepository responsableRestaurantRepository;

    private final ResponsableRestaurantMapper responsableRestaurantMapper;

    public ResponsableRestaurantQueryService(
        ResponsableRestaurantRepository responsableRestaurantRepository,
        ResponsableRestaurantMapper responsableRestaurantMapper
    ) {
        this.responsableRestaurantRepository = responsableRestaurantRepository;
        this.responsableRestaurantMapper = responsableRestaurantMapper;
    }

    /**
     * Return a {@link List} of {@link ResponsableRestaurantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ResponsableRestaurantDTO> findByCriteria(ResponsableRestaurantCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<ResponsableRestaurant> specification = createSpecification(criteria);
        return responsableRestaurantMapper.toDto(responsableRestaurantRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ResponsableRestaurantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ResponsableRestaurantDTO> findByCriteria(ResponsableRestaurantCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ResponsableRestaurant> specification = createSpecification(criteria);
        return responsableRestaurantRepository.findAll(specification, page).map(responsableRestaurantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ResponsableRestaurantCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<ResponsableRestaurant> specification = createSpecification(criteria);
        return responsableRestaurantRepository.count(specification);
    }

    /**
     * Function to convert {@link ResponsableRestaurantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ResponsableRestaurant> createSpecification(ResponsableRestaurantCriteria criteria) {
        Specification<ResponsableRestaurant> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ResponsableRestaurant_.id));
            }
            if (criteria.getNomResponsable() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getNomResponsable(), ResponsableRestaurant_.nomResponsable));
            }
            if (criteria.getPrenomResponsable() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getPrenomResponsable(), ResponsableRestaurant_.prenomResponsable));
            }
            if (criteria.getAdresseResponsable() != null) {
                specification =
                    specification.and(
                        buildStringSpecification(criteria.getAdresseResponsable(), ResponsableRestaurant_.adresseResponsable)
                    );
            }
            if (criteria.getNumResponsable() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getNumResponsable(), ResponsableRestaurant_.numResponsable));
            }
        }
        return specification;
    }
}
