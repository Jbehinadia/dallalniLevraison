package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ResponsableRestaurant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ResponsableRestaurant entity.
 */
@SuppressWarnings("")
@Repository
public interface ResponsableRestaurantRepository
    extends JpaRepository<ResponsableRestaurant, Long>, JpaSpecificationExecutor<ResponsableRestaurant> {}
