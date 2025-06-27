package org.gaung.wiwokdetok.kapsulkeaslian.factory;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.RegisterUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserFactory {

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(RegisterUserRequest request) {
       return buildBaseUser(request)
               .role("USER")
               .bio("")
               .profilePicture("http://example.com")
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
