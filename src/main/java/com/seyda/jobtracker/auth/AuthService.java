package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.auth.dto.AuthResponse;
import com.seyda.jobtracker.auth.dto.LoginRequest;
import com.seyda.jobtracker.auth.dto.RegisterRequest;
import com.seyda.jobtracker.user.User;
import com.seyda.jobtracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Şifreyi hashleyerek kaydediyoruz
                .build();

        userRepository.save(user);

        // Kullanıcı kaydedildikten sonra hemen token üretiyoruz
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Spring Security bizim yerimize e-posta ve şifrenin eşleşip eşleşmediğini kontrol eder
        // Eğer yanlışsa burada exception fırlatır ve kod aşağıya inmez
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Şifre doğruysa kullanıcıyı veritabanından çek ve token üret
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    // YENİ EKLENEN: Şifre Sıfırlama Metodu
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Bu mail adresine ait kullanıcı bulunamadı"));
        
        // Yeni şifreyi BCrypt ile şifreleyerek kaydediyoruz
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}