package org.wiwokdetok.kapsulkeaslian.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.entity.Follow;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.SimpleUserResponse;
import org.wiwokdetok.kapsulkeaslian.repository.FollowRepository;

import java.util.List;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowRepository followRepository;

    @Override
    public void followUser(String token, String id) {
        User fromUser = authenticationService.getUserFromToken(token);

        User toUser = userService.getUserById(id);

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
    public void unfollowUser(String token, String id) {
        User fromUser = authenticationService.getUserFromToken(token);

        User toUser = userService.getUserById(id);

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
