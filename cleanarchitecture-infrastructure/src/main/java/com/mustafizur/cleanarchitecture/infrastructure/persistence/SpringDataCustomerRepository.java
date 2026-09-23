package com.mustafizur.cleanarchitecture.infrastructure.persistence;

import com.mustafizur.cleanarchitecture.domain.customer.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, UUID> {
    Optional<CustomerJpaEntity> findByEmail(Email email);
    Page<CustomerJpaEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update CustomerJpaEntity c set c.deleted = true, c.updatedAt = CURRENT_TIMESTAMP where c.id = :id")
    int softDeleteById(@Param("id") UUID id);
}
