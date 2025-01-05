package com.zerobase.storereservation.domain.auth;

import com.zerobase.storereservation.domain.partner.PartnerEntity;
import com.zerobase.storereservation.domain.reservation.ReserveEntity;
import lombok.Data;

import java.util.List;

public class Auth {

    @Data
    public static class SignIn {
        private String userId;
        private String password;
    }


    @Data
    public static class SignUp {
        private String name;
        private String userId;
        private String password;
        private String phoneNumber;
        private List<String> roles;
        public PartnerEntity toPartnerEntity(){
            return PartnerEntity.builder()
                    .name(this.name)
                    .userId(this.userId)
                    .password(this.password)
                    .phoneNumber(this.phoneNumber)
                    .roles(this.roles)
                    .build();
        }


        public ReserveEntity toReserverEntity(){
            return ReserveEntity.builder()
                    .name(this.name)
                    .userId(this.userId)
                    .password(this.password)
                    .phoneNumber(this.phoneNumber)
                    .roles(this.roles)
                    .build();
        }
    }
}
