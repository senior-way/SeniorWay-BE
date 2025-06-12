package com.seniorway.seniorway.repository.profile;

import com.seniorway.seniorway.entity.profile.UserPreferredCategoryEntity;
import com.seniorway.seniorway.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPreferredCategoryRepository extends JpaRepository<UserPreferredCategoryEntity, Long> {
    List<UserPreferredCategoryEntity> findByUser(User user);

    // 필요하다면 category별 필터도 가능
    // List<UserPreferredCategoryEntity> findByCategory(CategoryEntity category);
}
