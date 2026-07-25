package com.citizenregistry.service.repository;

import com.citizenregistry.domain.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repository για την πρόσβαση στα δεδομένα των πολιτών.
 * Κληρονομεί τις βασικές CRUD λειτουργίες από JpaRepository
 * και τη δυναμική αναζήτηση από JpaSpecificationExecutor.
 */
@Repository
public interface CitizenRepository extends JpaRepository<Citizen, String>,
        JpaSpecificationExecutor<Citizen> {
}
