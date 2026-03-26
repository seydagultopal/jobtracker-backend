package com.seyda.jobtracker.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Ad boş bırakılamaz")
    private String firstName;
    
    @NotBlank(message = "Soyad boş bırakılamaz")
    private String lastName;
    
    @NotBlank(message = "E-posta boş bırakılamaz")
    @Email(message = "Lütfen geçerli bir e-posta adresi girin")
    private String email;
    
    @NotBlank(message = "Şifre boş bırakılamaz")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır")
    private String password;
}