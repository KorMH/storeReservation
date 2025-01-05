package com.zerobase.storereservation.sort;

import com.zerobase.storereservation.domain.store.Store;
import com.zerobase.storereservation.repository.StoreRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StoreSorter {

    private final StoreRepository storeRepository;

    public StoreSorter(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    // 가나다 순으로 정렬
    public Page<Store> getStoresOrderedByName(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        return storeRepository.findAll(pageable);
    }

    // 별점 순으로 정렬
    public Page<Store> getStoresOrderedByRating(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"));
        return storeRepository.findAll(pageable);
    }

    // 거리가 가까운 순으로 정렬

    /**
     *
     * @param page
     * @param size
     * @param userLat (사용자 위치(위도))
     * @param userLng (사용자 위치(경도))
     * @param radius (조회하고자 하는 반경(Km))
     * @return
     */
    public Page<Store> getStoresOrderedByDistance(int page, int size, double userLat, double userLng, double radius) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Store> stores = storeRepository.findAll(pageable);

        List<Store> filteredStores = stores.stream()
                .filter(store -> DistanceCalculator.calculateDistance(userLat, userLng, store.getLatitude(), store.getLongitude()) <= radius)
                .sorted(Comparator.comparing(store -> DistanceCalculator.calculateDistance(userLat, userLng, store.getLatitude(), store.getLongitude())))
                .collect(Collectors.toList());

        return new PageImpl<>(filteredStores, pageable, filteredStores.size());
    }
}
