package com.seniorway.seniorway.repository.trip;

import com.seniorway.seniorway.entity.trip.TripPlaceEntity;
import com.seniorway.seniorway.entity.trip.TripPlanEntity;
import com.seniorway.seniorway.entity.place.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripPlaceRepository extends JpaRepository<TripPlaceEntity, Long> {

    // 특정 여행(trip)에 속한 방문지 리스트 조회 (방문 순서 기준 오름차순)
    List<TripPlaceEntity> findByTripOrderByVisitOrderAsc(TripPlanEntity trip);

    // 특정 장소(place)를 방문하는 여행지 리스트 조회
    List<TripPlaceEntity> findByPlace(PlaceEntity place);
}
