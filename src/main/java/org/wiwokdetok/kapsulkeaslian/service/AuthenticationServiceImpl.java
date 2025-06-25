package org.wiwokdetok.kapsulkeaslian.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.RegisterUserRequest;
import org.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;

import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User authenticate(String email, String password) {
        String message = "Username atau password tidak valid";

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, message));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        }

        return user;
    }

    public void validatePasswordConfirm(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password tidak cocok");
        }
    }

    public void checkEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah terdaftar");
        }
    }

    public void registerUser(RegisterUserRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setBio("");
        user.setRole("USER");
        user.setProfilePicture("http://example.com");

        userRepository.save(user);
    }

    public User getUserFromToken(String token) {
        UUID userId = UUID.fromString(jwtTokenProvider.extractId(token.substring(7)));

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    }

    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public String generateToken(User user) {
        return jwtTokenProvider.generateToken(String.valueOf(user.getId()), user.getRole());
    }
}
