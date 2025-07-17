package org.gaung.wiwokdetok.kapsulkeaslian.factory;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.RegisterUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    @Value("${cloudflare.r2.public-endpoint}")
    private String publicEndpoint;

    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(RegisterUserRequest request) {
       return buildBaseUser(request)
               .role("USER")
               .bio("")
               .profilePicture(publicEndpoint + "/users/default.jpg")
               .build();
    }

    private User.UserBuilder buildBaseUser(RegisterUserRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName());
    }
}
