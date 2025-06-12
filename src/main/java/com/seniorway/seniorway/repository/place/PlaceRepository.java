package com.seniorway.seniorway.repository.place;

import com.seniorway.seniorway.entity.place.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    List<PlaceEntity> findByRegion(String region);

    // 예시 - 태그 포함 조회 (태그는 문자열로 되어 있으므로 like 쿼리 가능)
    List<PlaceEntity> findByTagsContaining(String tag);
}
