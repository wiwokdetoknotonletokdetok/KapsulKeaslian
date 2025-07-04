package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;
import org.springframework.data.domain.Page;

public interface FollowService {

    void followUser(String fromUserId, String toUserId);

    void unfollowUser(String fromUserId, String toUserId);

    Page<SimpleUserResponse> getUserFollowers(String id, int page, int size);

    Page<SimpleUserResponse> getUserFollowings(String id, int page, int size);
}
