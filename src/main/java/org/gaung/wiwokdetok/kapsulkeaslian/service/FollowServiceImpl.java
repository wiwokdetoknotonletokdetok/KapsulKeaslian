package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.Follow;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowRepository followRepository;

    @Override
    public void followUser(String fromUserId, String toUserId) {
        User fromUser = userService.getUserById(fromUserId);

        User toUser = userService.getUserById(toUserId);

        if (fromUser.equals(toUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tidak dapat mengikuti diri sendiri");
        }

        if (followRepository.existsByFollowerAndFollowing(fromUser, toUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sudah mengikuti pengguna ini");
        }

        Follow follow = new Follow(fromUser, toUser);
        followRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollowUser(String fromUserId, String toUserId) {
        User fromUser = userService.getUserById(fromUserId);

        User toUser = userService.getUserById(toUserId);

        if (!followRepository.existsByFollowerAndFollowing(fromUser, toUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Belum mengikuti pengguna ini");
        }

        followRepository.deleteByFollowerAndFollowing(fromUser, toUser);
    }

    @Override
    public List<SimpleUserResponse> getUserFollowers(String id) {
        User user = userService.getUserById(id);

        return followRepository.findFollowerUsers(user);
    }

    @Override
    public List<SimpleUserResponse> getUserFollowings(String id) {
        User user = userService.getUserById(id);

        return followRepository.findFollowingUsers(user);
    }
}
