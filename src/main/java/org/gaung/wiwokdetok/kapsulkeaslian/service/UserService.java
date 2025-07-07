package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdateUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserProfileResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;

import java.util.UUID;

public interface UserService {

    void updateUserProfile(UUID userId, UpdateUserRequest updateUserRequest);

    UserProfileResponse getUserProfile(UUID userId);

    User getUserById(UUID userId);
}
