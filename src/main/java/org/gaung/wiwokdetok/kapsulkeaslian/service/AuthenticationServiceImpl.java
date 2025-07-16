package org.gaung.wiwokdetok.kapsulkeaslian.service;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.LoginUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.RegisterUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdatePasswordRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.factory.UserFactory;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.gaung.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;
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
    public void registerUser(RegisterUserRequest request) {
        checkEmailExists(request.getEmail());

        validatePasswordConfirm(request.getPassword(), request.getConfirmPassword());

        saveUser(request);
    }

    private void checkEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email sudah terdaftar.");
        }
    }

    private void validatePasswordConfirm(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kata sandi dan konfirmasi tidak cocok.");
        }
    }

    private void saveUser(RegisterUserRequest request) {
        User user = userFactory.createUser(request);
        userRepository.save(user);
    }

    @Override
    public void updateUserPassword(UUID userId, UpdatePasswordRequest request) {
        User user = userService.getUserById(userId);

        validatePassword(user, request.getCurrentPassword(), "Kata sandi saat ini tidak valid.");

        validatePasswordConfirm(request.getNewPassword(), request.getConfirmNewPassword());

        validateNewPassword(request.getCurrentPassword(), request.getNewPassword());

        updatePassword(user, request.getNewPassword());
    }

    private void validatePassword(User user, String currentPassword, String message) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        }
    }

    private void validateNewPassword(String currentPassword, String newPassword) {
        if (currentPassword.equals(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kata sandi baru dan konfirmasi tidak cocok.");
        }
    }

    private void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
