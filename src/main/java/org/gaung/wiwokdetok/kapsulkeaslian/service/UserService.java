package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdateUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserProfileResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;

public interface UserService {

    void updateUserProfile(UpdateUserRequest updateUserRequest, String token);

    UserProfileResponse getUserProfile(String id);

    User getUserById(String id);

    User getUserFromToken(String token);
}
