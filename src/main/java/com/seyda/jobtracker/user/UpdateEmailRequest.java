package com.seyda.jobtracker.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailRequest {
    @NotBlank(message = "Yeni e-posta boş bırakılamaz")
    @Email(message = "Geçerli bir e-posta adresi girin")
    private String newEmail;

    @NotBlank(message = "Mevcut şifre boş bırakılamaz")
    private String currentPassword;
}
