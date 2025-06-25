package org.wiwokdetok.kapsulkeaslian.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.LoginUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.LoginUserResponse;
import org.wiwokdetok.kapsulkeaslian.model.RegisterUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UpdatePasswordRequest;
import org.wiwokdetok.kapsulkeaslian.model.WebResponse;
import org.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;
import org.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;

import java.util.UUID;

@RestController
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(
            path = "/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<LoginUserResponse>> login(
            @Valid @RequestBody LoginUserRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username atau password tidak valid"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username atau password tidak valid");
        }

        String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()), user.getRole());

        LoginUserResponse loginUserResponse = new LoginUserResponse();
        loginUserResponse.setToken(token);

        WebResponse<LoginUserResponse> response = WebResponse.<LoginUserResponse>builder()
                .data(loginUserResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            path = "/auth/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> register(
            @Valid @RequestBody RegisterUserRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password tidak cocok");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah terdaftar");
        }

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setBio("");
        user.setRole("USER");
        user.setProfilePicture("http://example.com");
        userRepository.save(user);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> logout() {

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @PatchMapping(
            path = "/auth/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> updatePassword(
            @RequestHeader(name = "Authorization", required = false) String token,
            @Valid @RequestBody UpdatePasswordRequest request) {

        UUID userId = UUID.fromString(jwtTokenProvider.extractId(token.substring(7)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password salah");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password tidak cocok");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }
}
