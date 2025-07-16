package org.gaung.wiwokdetok.kapsulkeaslian.service;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.Follow;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.FollowRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserService userService;

    private final FollowRepository followRepository;

    @Override
    public void followUser(UUID fromUserId, UUID toUserId) {
        User fromUser = userService.getUserById(fromUserId);

        User toUser = userService.getUserById(toUserId);

        if (fromUser.equals(toUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anda tidak dapat mengikuti diri sendiri.");
        }

        if (followRepository.existsByFollowerAndFollowing(fromUser, toUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anda sudah mengikuti pengguna ini.");
        }

        Follow follow = new Follow(fromUser, toUser);
        followRepository.save(follow);
    }

    @Override
    @Transactional
    public void unfollowUser(UUID fromUserId, UUID toUserId) {
        User fromUser = userService.getUserById(fromUserId);

        User toUser = userService.getUserById(toUserId);

        if (!followRepository.existsByFollowerAndFollowing(fromUser, toUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anda belum mengikuti pengguna ini.");
        }

        followRepository.deleteByFollowerAndFollowing(fromUser, toUser);
    }

    @Override
    public Page<SimpleUserResponse> getUserFollowers(UUID userId, int page, int size) {
        User user = userService.getUserById(userId);
        Pageable pageable = PageRequest.of(processPage(page), size);

        return followRepository.findFollowerUsers(user, pageable);
    }

    @Override
    public Page<SimpleUserResponse> getUserFollowings(UUID userId, int page, int size) {
        User user = userService.getUserById(userId);
        Pageable pageable = PageRequest.of(processPage(page), size);

        return followRepository.findFollowingUsers(user, pageable);
    }

    private int processPage(int page) {
        return page - 1;
    }
}
