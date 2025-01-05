package com.zerobase.storereservation.domain.review;

import com.zerobase.storereservation.domain.store.Store;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reviewerId;

    @Column(nullable = false)
    private LocalDate visitedDate;

    @Column(nullable = false)
    private LocalTime visitedTime;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "store_name", referencedColumnName = "name")
    private Store store;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewEntity review = (ReviewEntity) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
