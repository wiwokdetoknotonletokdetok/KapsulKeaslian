package org.wiwokdetok.kapsulkeaslian.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.wiwokdetok.kapsulkeaslian.entity.Follow;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.SimpleUserResponse;
import org.wiwokdetok.kapsulkeaslian.model.WebResponse;
import org.wiwokdetok.kapsulkeaslian.repository.FollowRepository;
import org.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;
import org.wiwokdetok.kapsulkeaslian.service.AuthenticationService;
import org.wiwokdetok.kapsulkeaslian.service.UserService;

import java.util.List;

@RestController
public class FollowController {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/users/{id}/follow",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> followUser(
            @RequestHeader(name = "Authorization", required = false) String token,
            @PathVariable("id") String id) {

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

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }

    @Transactional
    @AllowedRoles({"USER"})
    @DeleteMapping(
            path = "/users/{id}/follow",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> unfollowUser(
            @RequestHeader(name = "Authorization", required = false) String token,
            @PathVariable("id") String id) {

        User fromUser = authenticationService.getUserFromToken(token);

        User toUser = userService.getUserById(id);

        if (!followRepository.existsByFollowerAndFollowing(fromUser, toUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Belum mengikuti pengguna ini");
        }

        followRepository.deleteByFollowerAndFollowing(fromUser, toUser);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @GetMapping(
            path = "/users/{id}/followers",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<SimpleUserResponse>>> userFollowers(
            @PathVariable("id") String id) {

        User user = userService.getUserById(id);

        List<SimpleUserResponse> followers = followRepository.findFollowerUsers(user);

        WebResponse<List<SimpleUserResponse>> response = WebResponse.<List<SimpleUserResponse>>builder()
                .data(followers)
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @GetMapping(
            path = "/users/{id}/followings",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<SimpleUserResponse>>> userFollowings(
            @PathVariable("id") String id) {

        User user = userService.getUserById(id);

        List<SimpleUserResponse> followers = followRepository.findFollowingUsers(user);

        WebResponse<List<SimpleUserResponse>> response = WebResponse.<List<SimpleUserResponse>>builder()
                .data(followers)
                .build();

        return ResponseEntity.ok(response);
    }
}
