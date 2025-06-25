package org.wiwokdetok.kapsulkeaslian.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.UpdateUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UserProfileResponse;
import org.wiwokdetok.kapsulkeaslian.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public void validateEmailChange(User user, String newEmail) {
        if (!user.getEmail().equals(newEmail)) {
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah terdaftar");
            }
        }
    }

    public void updateUserData(User user, UpdateUserRequest request) {
        Optional.ofNullable(request.getEmail())
                .ifPresent(user::setEmail);

        Optional.ofNullable(request.getName())
                .ifPresent(user::setName);

        Optional.ofNullable(request.getBio()).
                ifPresent(user::setBio);

        userRepository.save(user);
    }

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

    public UserProfileResponse mapToUserProfileResponse(User user) {
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
}
