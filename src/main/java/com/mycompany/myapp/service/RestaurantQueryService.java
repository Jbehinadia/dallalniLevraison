package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Restaurant;
import com.mycompany.myapp.repository.RestaurantRepository;
import com.mycompany.myapp.service.criteria.RestaurantCriteria;
import com.mycompany.myapp.service.dto.RestaurantDTO;
import com.mycompany.myapp.service.mapper.RestaurantMapper;
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
 * Service for executing complex queries for {@link Restaurant} entities in the database.
 * The main input is a {@link RestaurantCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RestaurantDTO} or a {@link Page} of {@link RestaurantDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RestaurantQueryService extends QueryService<Restaurant> {

    private final Logger log = LoggerFactory.getLogger(RestaurantQueryService.class);

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMapper restaurantMapper;

    public RestaurantQueryService(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    /**
     * Return a {@link List} of {@link RestaurantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RestaurantDTO> findByCriteria(RestaurantCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Restaurant> specification = createSpecification(criteria);
        return restaurantMapper.toDto(restaurantRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RestaurantDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RestaurantDTO> findByCriteria(RestaurantCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Restaurant> specification = createSpecification(criteria);
        return restaurantRepository.findAll(specification, page).map(restaurantMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RestaurantCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Restaurant> specification = createSpecification(criteria);
        return restaurantRepository.count(specification);
    }

    /**
     * Function to convert {@link RestaurantCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Restaurant> createSpecification(RestaurantCriteria criteria) {
        Specification<Restaurant> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Restaurant_.id));
            }
            if (criteria.getNomRestaurant() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNomRestaurant(), Restaurant_.nomRestaurant));
            }
            if (criteria.getAdresseRestaurant() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAdresseRestaurant(), Restaurant_.adresseRestaurant));
            }
            if (criteria.getNumRestaurant() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNumRestaurant(), Restaurant_.numRestaurant));
            }
            if (criteria.getDateOuverture() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateOuverture(), Restaurant_.dateOuverture));
            }
            if (criteria.getDateFermiture() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDateFermiture(), Restaurant_.dateFermiture));
            }
            if (criteria.getResponsableRestaurantId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getResponsableRestaurantId(),
                            root -> root.join(Restaurant_.responsableRestaurant, JoinType.LEFT).get(ResponsableRestaurant_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
