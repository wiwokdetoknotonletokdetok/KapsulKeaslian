package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdateUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserProfileResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserRankingResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void updateUserProfile(String id, UpdateUserRequest request) {
        User user = getUserById(id);

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
    @Transactional
    public void addPoints(String userId, int pointsToAdd) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPoints(user.getPoints() + pointsToAdd);
        userRepository.save(user);
    }

    @Override
    public List<UserRankingResponse> getUserRanking() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "points"));

        return users.stream()
                .map(user -> new UserRankingResponse(user.getId().toString(), user.getName(), user.getPoints()))
                .collect(Collectors.toList());
    }
}
