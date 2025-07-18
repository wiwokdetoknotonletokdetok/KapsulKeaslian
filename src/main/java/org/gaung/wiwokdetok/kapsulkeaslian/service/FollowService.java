package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface FollowService {

    boolean followStatus(UUID fromUserId, UUID toUserId);

    void followUser(UUID fromUserId, UUID toUserId);

    void unfollowUser(UUID fromUserId, UUID toUserId);

    Page<SimpleUserResponse> getUserFollowers(UUID userId, int page, int size);

    Page<SimpleUserResponse> getUserFollowings(UUID userId, int page, int size);
}
