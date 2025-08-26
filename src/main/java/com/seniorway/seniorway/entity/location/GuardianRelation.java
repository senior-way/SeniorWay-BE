package com.seniorway.seniorway.entity.location;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guardian_relation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long GuardianRelationId;

    private Long guardianId;
    private Long wardId;
}
