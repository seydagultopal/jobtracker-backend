package com.seyda.jobtracker.user;

import com.seyda.jobtracker.auth.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(RegisterRequest request) {
        
        // E-posta adresi daha önce kullanılmış mı kontrolü
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda: " + request.getEmail());
        }
        
        // DTO'dan gelen verilerle yeni bir User Entity oluşturulması
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword()) // Gün 3'te burayı encode edilmiş şifreyle değiştireceğiz
                .build();
        
        return userRepository.save(user);
    }
}