package org.gaung.wiwokdetok.kapsulkeaslian.service;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.AmqpUserRegisteredMessage;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.LoginUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.RegisterUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdatePasswordRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.factory.UserFactory;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.publisher.UserActivityPublisher;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.gaung.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;

    private final UserRepository userRepository;

    private final UserFactory userFactory;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final UserActivityPublisher userActivityPublisher;

    @Value("${application.base-url}")
    private String applicationBaseUrl;

    @Override
    public LoginUserResponse authenticate(String email, String password) {
        User user = getUserByEmailAndPassword(email, password);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole());

        return new LoginUserResponse(token);
    }

    private User getUserByEmailAndPassword(String email, String password) {
        String message = "Email atau kata sandi salah.";

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, message));

        validatePassword(user, password, message);

        return user;
    }

    @Override
    public String registerUser(RegisterUserRequest request) {
        checkEmailExists(request.getEmail());
        User user = saveUser(request);
        sendUserRegisteredMessage(user);

        return String.format("%s/users/%s", applicationBaseUrl, user.getId());
    }

    private void sendUserRegisteredMessage(User user) {
        AmqpUserRegisteredMessage message = new AmqpUserRegisteredMessage(user.getId());
        userActivityPublisher.sendUserRegisteredMessage(message);
    }

    private void checkEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email sudah terdaftar.");
        }
    }

    private User saveUser(RegisterUserRequest request) {
        User user = userFactory.createUser(request);
        userRepository.save(user);

        return user;
    }

    @Override
    public void updateUserPassword(UUID userId, UpdatePasswordRequest request) {
        User user = userService.getUserById(userId);

        validatePassword(user, request.getCurrentPassword(), "Kata sandi saat ini tidak valid.");

        updatePassword(user, request.getNewPassword());
    }

    private void validatePassword(User user, String currentPassword, String message) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        }
    }

    private void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
