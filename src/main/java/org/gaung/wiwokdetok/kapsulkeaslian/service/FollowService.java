package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;

import java.util.List;

public interface FollowService {

    void followUser(String fromUserId, String toUserId);

    void unfollowUser(String fromUserId, String toUserId);

    List<SimpleUserResponse> getUserFollowers(String id);

    List<SimpleUserResponse> getUserFollowings(String id);
}
