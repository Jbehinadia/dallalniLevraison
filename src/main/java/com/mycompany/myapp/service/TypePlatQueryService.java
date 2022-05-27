package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.TypePlat;
import com.mycompany.myapp.repository.TypePlatRepository;
import com.mycompany.myapp.service.criteria.TypePlatCriteria;
import com.mycompany.myapp.service.dto.TypePlatDTO;
import com.mycompany.myapp.service.mapper.TypePlatMapper;
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
 * Service for executing complex queries for {@link TypePlat} entities in the database.
 * The main input is a {@link TypePlatCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TypePlatDTO} or a {@link Page} of {@link TypePlatDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TypePlatQueryService extends QueryService<TypePlat> {

    private final Logger log = LoggerFactory.getLogger(TypePlatQueryService.class);

    private final TypePlatRepository typePlatRepository;

    private final TypePlatMapper typePlatMapper;

    public TypePlatQueryService(TypePlatRepository typePlatRepository, TypePlatMapper typePlatMapper) {
        this.typePlatRepository = typePlatRepository;
        this.typePlatMapper = typePlatMapper;
    }

    /**
     * Return a {@link List} of {@link TypePlatDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TypePlatDTO> findByCriteria(TypePlatCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TypePlat> specification = createSpecification(criteria);
        return typePlatMapper.toDto(typePlatRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TypePlatDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TypePlatDTO> findByCriteria(TypePlatCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TypePlat> specification = createSpecification(criteria);
        return typePlatRepository.findAll(specification, page).map(typePlatMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TypePlatCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TypePlat> specification = createSpecification(criteria);
        return typePlatRepository.count(specification);
    }

    /**
     * Function to convert {@link TypePlatCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TypePlat> createSpecification(TypePlatCriteria criteria) {
        Specification<TypePlat> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TypePlat_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), TypePlat_.type));
            }
        }
        return specification;
    }
}
