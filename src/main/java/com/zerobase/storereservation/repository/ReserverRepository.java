package com.zerobase.storereservation.repository;

import com.zerobase.storereservation.domain.reservation.ReserveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReserverRepository extends JpaRepository<ReserveEntity, Long> {
    Optional<ReserveEntity> findByUserId(String userId);

    boolean existsByUserId(String userId);
}