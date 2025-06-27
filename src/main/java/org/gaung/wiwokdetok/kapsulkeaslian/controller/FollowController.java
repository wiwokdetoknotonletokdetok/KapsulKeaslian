package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.WebResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.kapsulkeaslian.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FollowController {

    @Autowired
    private FollowService followService;

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/users/{id}/follow",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> followUser(
            @RequestHeader(name = "Authorization", required = false) String token,
            @PathVariable("id") String id) {

        followService.followUser(token, id);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @DeleteMapping(
            path = "/users/{id}/follow",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> unfollowUser(
            @RequestHeader(name = "Authorization", required = false) String token,
            @PathVariable("id") String id) {

        followService.unfollowUser(token, id);

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

        List<SimpleUserResponse> followers = followService.getUserFollowers(id);

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

        List<SimpleUserResponse> followings = followService.getUserFollowings(id);

        WebResponse<List<SimpleUserResponse>> response = WebResponse.<List<SimpleUserResponse>>builder()
                .data(followings)
                .build();

        return ResponseEntity.ok(response);
    }
}
