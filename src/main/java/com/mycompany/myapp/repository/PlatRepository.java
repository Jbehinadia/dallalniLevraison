package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Plat;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Plat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlatRepository extends JpaRepository<Plat, Long>, JpaSpecificationExecutor<Plat> {}
