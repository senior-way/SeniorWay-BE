package com.seniorway.seniorway.entity.profile;

import com.seniorway.seniorway.entity.User;
import com.seniorway.seniorway.entity.place.CategoryEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_preferred_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferredCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;
}