package com.seyda.jobtracker.user;

import com.seyda.jobtracker.auth.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu e-posta adresi zaten kullanımda: " + request.getEmail());
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword()) 
                .firstName(request.getFirstName()) 
                .lastName(request.getLastName())
                .build();
        
        return userRepository.save(user);
    }

    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));
    }

    @Transactional
    public User updateProfile(Long userId, User updatedData) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        existingUser.setFirstName(updatedData.getFirstName());
        existingUser.setLastName(updatedData.getLastName());
        existingUser.setPhone(updatedData.getPhone());
        existingUser.setLocation(updatedData.getLocation());
        existingUser.setTitle(updatedData.getTitle());
        
        existingUser.setLinkedin(updatedData.getLinkedin());
        existingUser.setGithub(updatedData.getGithub());
        
        existingUser.setBio(updatedData.getBio());
        
        if (updatedData.getPhoto() != null) {
            existingUser.setPhoto(updatedData.getPhoto());
        }
        
        if (updatedData.getCoverPhoto() != null) {
            existingUser.setCoverPhoto(updatedData.getCoverPhoto());
        }

        // YENİ: CV Verilerini kaydet
        if (updatedData.getExperiences() != null) {
            existingUser.setExperiences(updatedData.getExperiences());
        }
        if (updatedData.getEducations() != null) {
            existingUser.setEducations(updatedData.getEducations());
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public void updateEmail(Long userId, String newEmail, String currentPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Mevcut şifre yanlış!");
        }

        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Bu e-posta adresi başka bir hesap tarafından kullanılıyor.");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Mevcut şifre yanlış!");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}