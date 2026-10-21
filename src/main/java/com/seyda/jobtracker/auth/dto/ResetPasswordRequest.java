package com.seyda.jobtracker.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Sıfırlama token'ı boş bırakılamaz")
    private String token;

    @NotBlank(message = "Yeni şifre boş bırakılamaz")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır")
    private String newPassword;
}
