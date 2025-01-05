package com.zerobase.storereservation.domain.reservation;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zerobase.storereservation.domain.store.Store;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "reservation")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false )
    private LocalDate reservationDate;

    @Column(nullable = false)
    private String reservationPhoneNumber;

    @Column(nullable = false)
    private LocalTime reservationTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

}
