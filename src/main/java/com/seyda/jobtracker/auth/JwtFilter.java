package com.seyda.jobtracker.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // İstek başlığında token yoksa veya "Bearer " ile başlamıyorsa diğer filtrelere geç
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " kelimesi 7 karakter olduğu için 7. indeksten sonrasını alıyoruz (sadece token)
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // Token'dan e-posta çıktıysa ve kullanıcı henüz sisteme giriş yapmamışsa (context boşsa)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Token geçerliyse kullanıcıyı sisteme "Giriş Yaptı" olarak işaretle
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Güvenlik bağlamına (context) kullanıcıyı yerleştir
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // İşlem bittikten sonra isteği yoluna devam ettir
        filterChain.doFilter(request, response);
    }
}