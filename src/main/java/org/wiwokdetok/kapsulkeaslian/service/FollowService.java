package org.wiwokdetok.kapsulkeaslian.service;

import org.wiwokdetok.kapsulkeaslian.model.SimpleUserResponse;

import java.util.List;

public interface FollowService {

    void followUser(String token, String id);

    void unfollowUser(String token, String id);

    List<SimpleUserResponse> getUserFollowers(String id);

    List<SimpleUserResponse> getUserFollowings(String id);
}
