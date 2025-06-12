package com.seniorway.seniorway.repository.place;

import com.seniorway.seniorway.entity.place.PlaceCategoryEntity;
import com.seniorway.seniorway.entity.place.PlaceEntity;
import com.seniorway.seniorway.entity.place.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceCategoryRepository extends JpaRepository<PlaceCategoryEntity, Long> {

    // 특정 장소에 연결된 카테고리들 조회
    List<PlaceCategoryEntity> findByPlace(PlaceEntity place);

    // 특정 카테고리에 포함된 장소들 조회
    List<PlaceCategoryEntity> findByCategory(CategoryEntity category);
}
