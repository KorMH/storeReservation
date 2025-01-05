package com.zerobase.storereservation.service;

import com.zerobase.storereservation.domain.auth.Auth;
import com.zerobase.storereservation.domain.reservation.ReservationEntity;
import com.zerobase.storereservation.domain.reservation.ReserveEntity;
import com.zerobase.storereservation.domain.store.Store;
import com.zerobase.storereservation.dto.ReservationRequest;
import com.zerobase.storereservation.exception.exceptiomimpl.AlreadyExistUserException;
import com.zerobase.storereservation.exception.exceptiomimpl.StoreNotFoundException;
import com.zerobase.storereservation.repository.ReservationRepository;
import com.zerobase.storereservation.repository.ReserverRepository;
import com.zerobase.storereservation.repository.StoreRepository;
import com.zerobase.storereservation.sort.StoreSorter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ReserverService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final ReserverRepository reserverRepository;
    private final StoreRepository storeRepository;
    private final ReservationRepository reservationRepository;
    private final StoreSorter storeSorter;

    // UserDetails를 로드하는 메소드
    // Spring Security에서 사용자 인증을 처리하는 데 사용
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return this.reserverRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + userId));
    }

    // 사용자(예약자) 등록을 위한 메소드
    public ReserveEntity register(Auth.SignUp reserver){
        // 이미 존재하는 ID인지 확인
        boolean exists = this.reserverRepository.existsByUserId(reserver.getUserId());
        if(exists) {
            throw new AlreadyExistUserException();
        }

        // 사용자의 비밀번호를 암호화하여 저장
        // 비밀번호를 평문으로 저장되지 않아 보안을 유지함
        reserver.setPassword(this.passwordEncoder.encode(reserver.getPassword()));
        var result = this.reserverRepository.save(reserver.toReserverEntity());
        return result;
    }

    // 사용자 정보 수정을 위한 메소드
    public ReserveEntity editUserInfo(Auth.SignUp reserver){
        ReserveEntity currentReserver = getCurrentReserver();
        currentReserver.updateInfo(reserver);
        reserverRepository.save(currentReserver);
        return currentReserver;
    }

    // 사용자 계정을 삭제하기 위한 메소드
    public String deleteAccount(){
        ReserveEntity currentReserver = getCurrentReserver();
        reserverRepository.delete(currentReserver);
        return "Account deleted successfully";
    }

    // 사용자에 대한 정보가 유효한지 확인하는 메소드
    public ReserveEntity authenticate(Auth.SignIn reserver){
        var user = this.reserverRepository.findByUserId(reserver.getUserId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자 입니다."));

        if(!this.passwordEncoder.matches(reserver.getPassword(), user.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    // 상점 이름으로 상점에 대한 상세 정보를 조회하는 메소드
    public Store search(String storeName){
        var store = this.storeRepository.findByName(storeName)
                .orElseThrow(StoreNotFoundException::new);

        return store;
    }

    public Page<Store> sortStores(String criteria, int page, int size, Double userLat, Double userLng, Double radius) {
        // 주어진 정렬 기준에 따라 상점 목록을 정렬하고 반환
        return switch (criteria) {
            // 정렬 기준이 'name'인 경우, 상점 이름 순으로 정렬
            case "name" -> storeSorter.getStoresOrderedByName(page, size);
            // 정렬 기준이 'rating'인 경우, 상점 평점 순으로 정렬
            case "rating" -> storeSorter.getStoresOrderedByRating(page, size);
            // 정렬 기준이 'distance'인 경우, 사용자의 위치에서 상점까지의 거리 순으로 정렬
            // 이 경우, 사용자의 위도, 경도, 그리고 조회 반경이 제공되어야 함
            case "distance" -> {
                if (userLat == null || userLng == null || radius == null) {
                    throw new IllegalArgumentException("User latitude, longitude and radius must be provided for distance sort");
                }
                yield storeSorter.getStoresOrderedByDistance(page, size, userLat, userLng, radius);
            }
            // 제공된 정렬 기준이 유효하지 않은 경우, 예외를 발생시킴
            default ->
                    throw new IllegalArgumentException("Invalid sort criteria");
        };
    }

    // 예약 생성을 수행하는 메소드
    public ReservationEntity makeReservation(ReservationRequest request) {
        // 현재 사용자 정보를 가져오기
        ReserveEntity currentReserver = getCurrentReserver();

        // 요청에서 상점 이름을 가져와 해당 상점을 조회
        // 상점을 찾을 수 없는 경우 예외를 발생시킴
        Store store = this.storeRepository.findByName(request.getStoreName())
                .orElseThrow(StoreNotFoundException::new);

        // 예약 날짜 가져오기
        LocalDate reservationDate = request.getDate();

        // 동일한 상점과 예약 날짜에 대한 기존 예약이 있는지 확인. 만약 있다면 예외를 발생시킴.
        Optional<ReservationEntity> existingReservation = this.reservationRepository
                .findByStoreAndReservationDate(store, reservationDate);
        if (existingReservation.isPresent()) {
            throw new RuntimeException("There is already a reservation at this time.");
        }

        // 새로운 예약을 생성하고 저장
        ReservationEntity reservation = ReservationEntity.builder()
                .reservationPhoneNumber(currentReserver.getPhoneNumber())
                .reservationDate(request.getDate())
                .reservationTime(request.getTime())
                .store(store)
                .status(true)
                .build();

        ReservationEntity savedReservation = this.reservationRepository.save(reservation);

        // 저장된 예약을 반환
        return savedReservation;
    }

    // 매장 도착 정보를 받고 유효한 예약인지 확인하는 메소드
    public String arrivalConfirm(String storeName, LocalDate date, LocalTime time, String phoneNumber){
        // 상점 이름, 예약 날짜, 예약 시간, 전화번호를 기반으로 예약을 조회
        Optional<ReservationEntity> optionalReservation = this.reservationRepository
                .findByStore_NameAndReservationDateAndReservationTimeAndReservationPhoneNumber(
                        storeName,
                        date,
                        time,
                        phoneNumber
                );

        // 해당 예약이 없는 경우 예외를 발생
        if (optionalReservation.isEmpty()) {
            throw new RuntimeException("No such reservation");
        }

        // 현재 날짜와 시간 가져오기
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        /*
        예약 날짜가 오늘이 아니거나,
        현재 시간이 예약 시간 이후거나,
        10분전에 도착해서 도착확인을 해야 하는데 10분 전이 아니라 이 이후에 오는 경우,
        예약 상태가 false인 경우(점주에 의해 예약이 거절당한 경우) 예외를 발생시킴
        */
        if (!today.equals(date)
                || currentTime.isAfter(time)
                || currentTime.plusMinutes(10).isAfter(time)
                || optionalReservation.get().getStatus() == false
        ) {
            throw new RuntimeException("Invalid Confirmation");
        }

        // 예약 확인이 완료되었음을 반환
        return "confirmed!";
    }

    // 현재 인증된 사용자를 반환하는 메소드
    private ReserveEntity getCurrentReserver() {
        return (ReserveEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}