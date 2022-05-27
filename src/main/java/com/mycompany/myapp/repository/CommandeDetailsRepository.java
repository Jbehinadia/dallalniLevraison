package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CommandeDetails;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CommandeDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommandeDetailsRepository extends JpaRepository<CommandeDetails, Long>, JpaSpecificationExecutor<CommandeDetails> {}
