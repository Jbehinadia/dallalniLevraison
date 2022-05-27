package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TypePlat;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TypePlat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypePlatRepository extends JpaRepository<TypePlat, Long>, JpaSpecificationExecutor<TypePlat> {}
