package org.wiwokdetok.kapsulkeaslian.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.UpdateUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UserProfileResponse;
import org.wiwokdetok.kapsulkeaslian.model.WebResponse;
import org.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;
import org.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;

import java.util.Optional;
import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @AllowedRoles({"USER"})
    @PatchMapping(
            path = "/users/me",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> updateUser(
            @RequestHeader(name = "Authorization", required = false) String token,
            @Valid @RequestBody UpdateUserRequest request) {

        UUID userId = UUID.fromString(jwtTokenProvider.extractId(token.substring(7)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));

        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah terdaftar");
            }
        }

        Optional.ofNullable(request.getEmail())
                .ifPresent(user::setEmail);

        Optional.ofNullable(request.getName())
                .ifPresent(user::setName);

        Optional.ofNullable(request.getBio())
                .ifPresent(user::setBio);

        userRepository.save(user);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @GetMapping(
            path = "/users/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserProfileResponse>> userProfile(
            @PathVariable("id") String id) {

        Optional<User> user = userRepository.findById(UUID.fromString(id));

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan");
        }

        UserProfileResponse userProfile = UserProfileResponse.builder()
                .email(user.get().getEmail())
                .name(user.get().getName())
                .bio(user.get().getBio())
                .profilePicture(user.get().getProfilePicture())
                .followers(user.get().getFollowers())
                .followings(user.get().getFollowings())
                .points(user.get().getPoints())
                .build();

        WebResponse<UserProfileResponse> response = WebResponse.<UserProfileResponse>builder()
                .data(userProfile)
                .build();

        return ResponseEntity.ok(response);
    }
}
