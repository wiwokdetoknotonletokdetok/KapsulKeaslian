package org.wiwokdetok.kapsulkeaslian.service;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.UpdateUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UserProfileResponse;
import org.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void updateUserProfile(UpdateUserRequest request, String token) {
        User user = getUserFromToken(token);

        validateEmailChange(user, request.getEmail());

        updateUser(user, request);
    }

    private void validateEmailChange(User user, String newEmail) {
        if (!user.getEmail().equals(newEmail)) {
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah terdaftar");
            }
        }
    }

    private void updateUser(User user, UpdateUserRequest request) {
        Optional.ofNullable(request.getEmail())
                .ifPresent(user::setEmail);

        Optional.ofNullable(request.getName())
                .ifPresent(user::setName);

        Optional.ofNullable(request.getBio()).
                ifPresent(user::setBio);

        userRepository.save(user);
    }

    @Override
    public UserProfileResponse getUserProfile(String id) {
        User user = getUserById(id);

        return mapToUserProfileResponse(user);
    }

    @Override
    public User getUserById(String id) {
        UUID userId;
        try {
            userId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID user tidak valid");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .bio(user.getBio())
                .profilePicture(user.getProfilePicture())
                .followers(user.getFollowers())
                .followings(user.getFollowings())
                .points(user.getPoints())
                .build();
    }

    @Override
    public User getUserFromToken(String token) {
        Claims payload = jwtTokenProvider.decodeToken(token.substring(7));
        UUID userId = UUID.fromString(jwtTokenProvider.getId(payload));

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User tidak ditemukan"));
    }
}
