package org.gaung.wiwokdetok.kapsulkeaslian.factory;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.RegisterUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserFactory {

    @Value("${CLOUDFLARE_R2_PUBLIC_ENDPOINT}")
    private String publicEndpoint;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(RegisterUserRequest request) {
       return buildBaseUser(request)
               .role("USER")
               .bio("")
               .profilePicture(publicEndpoint + "/default.jpg")
               .build();
    }

    private User.UserBuilder buildBaseUser(RegisterUserRequest request) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName());
    }
}
