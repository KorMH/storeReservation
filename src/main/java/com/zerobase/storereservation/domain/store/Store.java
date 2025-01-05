package com.zerobase.storereservation.domain.store;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zerobase.storereservation.domain.partner.PartnerEntity;
import com.zerobase.storereservation.domain.reservation.ReservationEntity;
import com.zerobase.storereservation.domain.review.ReviewEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "store")
public class Store implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;


    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private String description;

    @Column(nullable = false)
    private Double rating;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "partner_id")
    private PartnerEntity partner;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationEntity> reservations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews = new ArrayList<>();


}
