package com.zerobase.storereservation.repository;

import com.zerobase.storereservation.domain.reservation.ReservationEntity;
import com.zerobase.storereservation.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    Optional<ReservationEntity> findByStoreAndReservationDate(
            Store store,
            LocalDate reservationDate
    );

    List<ReservationEntity> findByStoreNameAndReservationDateOrderByReservationTimeAsc(
            String storeName,
            LocalDate reservationDate
    );

    Optional<ReservationEntity> findByStore_NameAndReservationDateAndReservationTimeAndReservationPhoneNumber(
            String storeName,
            LocalDate reservationDate,
            LocalTime reservationTime,
            String reservationPhoneNumber
    );

    Optional<ReservationEntity> findByStore_NameAndReservationDateAndReservationTime(
            String storeName,
            LocalDate reservationDate,
            LocalTime reservationTime
    );
}