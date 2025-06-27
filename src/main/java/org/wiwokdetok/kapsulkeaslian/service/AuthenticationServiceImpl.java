package org.wiwokdetok.kapsulkeaslian.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.factory.UserFactory;
import org.wiwokdetok.kapsulkeaslian.model.LoginUserResponse;
import org.wiwokdetok.kapsulkeaslian.model.RegisterUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UpdatePasswordRequest;
import org.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginUserResponse authenticate(String email, String password) {
        User user = getUserByEmailAndPassword(email, password);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return new LoginUserResponse(token);
    }

    private User getUserByEmailAndPassword(String email, String password) {
        String message = "Username atau password tidak valid";

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah terdaftar");
        }
    }

    private void validatePasswordConfirm(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password tidak cocok");
        }
    }

    private void saveUser(RegisterUserRequest request) {
        User user = userFactory.createUser(request);
        userRepository.save(user);
    }

    @Override
    public void updateUserPassword(String token, UpdatePasswordRequest request) {
        User user = userService.getUserFromToken(token);

        validatePassword(user, request.getCurrentPassword(), "Password tidak valid");

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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password baru identik dengan yang lama");
        }
    }

    private void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
