package com.seyda.jobtracker.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // E-posta adresine göre kullanıcıyı bulmak için Spring Data JPA'nın sihirli metodunu yazıyoruz.
    // İleride Login işleminde bu metoda çok ihtiyacımız olacak.
    Optional<User> findByEmail(String email);
    
    // Bir e-postanın sistemde kayıtlı olup olmadığını hızlıca kontrol etmek için.
    boolean existsByEmail(String email);
}