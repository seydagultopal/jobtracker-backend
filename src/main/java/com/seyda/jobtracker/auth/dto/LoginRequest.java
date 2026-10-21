package com.seyda.jobtracker.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "E-posta boş bırakılamaz")
    @Email(message = "Lütfen geçerli bir e-posta adresi girin")
    private String email;

    @NotBlank(message = "Şifre boş bırakılamaz")
    private String password;
}