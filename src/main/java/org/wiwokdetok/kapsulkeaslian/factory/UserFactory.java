package org.wiwokdetok.kapsulkeaslian.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.RegisterUserRequest;

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
