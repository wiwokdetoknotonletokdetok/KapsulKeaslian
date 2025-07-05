package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdateUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserProfileResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserRankingResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;

import java.util.List;

public interface UserService {

    void updateUserProfile(String id, UpdateUserRequest updateUserRequest);

    UserProfileResponse getUserProfile(String id);

    User getUserById(String id);
    void addPoints(String id, int points);
    List<UserRankingResponse> getUserRanking();
}
