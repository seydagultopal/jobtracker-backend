package com.seyda.jobtracker.auth;

import com.seyda.jobtracker.user.User;
import com.seyda.jobtracker.user.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;
    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User googleUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = googleUser.getAttributes();
        String email = text(attributes, "email");
        String subject = text(attributes, "sub");
        if (email == null || subject == null || !Boolean.TRUE.equals(attributes.get("email_verified"))) {
            redirect(response, "login?oauthError=unverified_email");
            return;
        }

        User user = userRepository.findByGoogleSubject(subject).orElse(null);
        if (user == null) {
            // Google has verified this email. Link the identity to an existing Pebble
            // account instead of treating a sign-in attempt as a new registration.
            user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                if (user.getGoogleSubject() != null && !user.getGoogleSubject().equals(subject)) {
                    redirect(response, "login?oauthError=account_exists");
                    return;
                }
                user.setGoogleSubject(subject);
                try {
                    user = userRepository.saveAndFlush(user);
                } catch (DataIntegrityViolationException exception) {
                    redirect(response, "login?oauthError=account_exists");
                    return;
                }
            } else {
                byte[] bytes = new byte[48];
                RANDOM.nextBytes(bytes);
                user = User.builder().email(email)
                        .password(passwordEncoder.encode(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)))
                        .firstName(text(attributes, "given_name"))
                        .lastName(text(attributes, "family_name"))
                        .photo(text(attributes, "picture"))
                        .authProvider("GOOGLE").googleSubject(subject).build();
                try {
                    user = userRepository.saveAndFlush(user);
                } catch (DataIntegrityViolationException exception) {
                    redirect(response, "login?oauthError=account_exists");
                    return;
                }
            }
        }

        String jwt = jwtService.generateToken(user);
        ResponseCookie cookie = ResponseCookie.from("PEBBLE_AUTH", jwt)
                .httpOnly(true).secure(secureCookie).sameSite("Strict").path("/")
                .maxAge(Duration.ofHours(24)).build();
        response.addHeader("Set-Cookie", cookie.toString());

        // OAuth2 stores an OAuth2User in the server session. The application APIs
        // expect the User entity loaded by JwtFilter, so end the OAuth session and
        // let subsequent requests authenticate using the JWT cookie.
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        redirect(response, "dashboard");
    }

    private static String text(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        return value instanceof String string && !string.isBlank() ? string : null;
    }

    private void redirect(HttpServletResponse response, String path) throws IOException {
        response.sendRedirect(frontendUrl.replaceAll("/+$", "") + "/" + path);
    }
}
