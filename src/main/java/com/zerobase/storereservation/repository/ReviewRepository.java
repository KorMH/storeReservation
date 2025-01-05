package com.zerobase.storereservation.repository;


import com.zerobase.storereservation.domain.review.ReviewEntity;
import com.zerobase.storereservation.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByStoreName(String storeName);

    Optional<ReviewEntity> findByReviewerIdAndStoreNameAndVisitedDateAndVisitedTime(
            String reviewerId,
            String storeName,
            LocalDate visitedDate,
            LocalTime visitedTime
    );

    List<ReviewEntity> findByStore(Store store);
}