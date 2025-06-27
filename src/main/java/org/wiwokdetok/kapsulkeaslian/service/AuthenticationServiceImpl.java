package org.wiwokdetok.kapsulkeaslian.service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.LoginUserResponse;
import org.wiwokdetok.kapsulkeaslian.model.RegisterUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UpdatePasswordRequest;
import org.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;

import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginUserResponse authenticate(String email, String password) {
        User user = getUserByEmailAndPassword(email, password);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        LoginUserResponse loginUserResponse = new LoginUserResponse();
        loginUserResponse.setToken(token);

        return loginUserResponse;
    }

    private User getUserByEmailAndPassword(String email, String password) {
        String message = "Username atau password tidak valid";

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, message));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        }

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

    @Override
    public void updateUserPassword(String token, UpdatePasswordRequest request) {
        User user = userService.getUserFromToken(token);

        user = getUserByEmailAndPassword(user.getEmail(), request.getCurrentPassword());

        validatePasswordConfirm(request.getNewPassword(), request.getConfirmNewPassword());

        validateNewPassword(request.getCurrentPassword(), request.getNewPassword());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void validateNewPassword(String currentPassword, String newPassword) {
        if (currentPassword.equals(newPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password baru identik dengan yang lama");
        }
    }
}
