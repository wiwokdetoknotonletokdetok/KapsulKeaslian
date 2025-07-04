package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.Follow;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    public Page<SimpleUserResponse> getUserFollowers(String id, int page, int size) {
        User user = userService.getUserById(id);
        Pageable pageable = PageRequest.of(processPage(page), size);

        return followRepository.findFollowerUsers(user, pageable);
    }

    @Override
    public Page<SimpleUserResponse> getUserFollowings(String id, int page, int size) {
        User user = userService.getUserById(id);
        Pageable pageable = PageRequest.of(processPage(page), size);

        return followRepository.findFollowingUsers(user, pageable);
    }

    private int processPage(int page) {
        return page - 1;
    }
}
