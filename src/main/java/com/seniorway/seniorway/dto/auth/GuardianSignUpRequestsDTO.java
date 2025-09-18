package com.seniorway.seniorway.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuardianSignUpRequestsDTO {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;
    
    private String password;
    private String passwordConfirm;

    @NotBlank(message = "이름은 필수입니다")
    private String username;
}
