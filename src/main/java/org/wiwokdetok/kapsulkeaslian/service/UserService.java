package org.wiwokdetok.kapsulkeaslian.service;

import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.UpdateUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UserProfileResponse;

public interface UserService {

    void updateUserProfile(UpdateUserRequest updateUserRequest, String token);

    UserProfileResponse getUserProfile(String id);

    User getUserById(String id);

    User getUserFromToken(String token);
}
