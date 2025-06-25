package org.wiwokdetok.kapsulkeaslian.service;

import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.UpdateUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UserProfileResponse;

public interface UserService {

    void validateEmailChange(User user, String newEmail);

    void updateUserData(User user, UpdateUserRequest request);

    User getUserById(String id);

    UserProfileResponse mapToUserProfileResponse(User user);
}
