package com.seniorway.seniorway.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEmailDto {

    @NotNull(message = "userEmail은 필수입니다")
    private String userEmail;
}
