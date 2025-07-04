package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import jakarta.validation.Valid;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdateUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserPrincipal;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserProfileResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.WebResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.kapsulkeaslian.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @AllowedRoles({"USER"})
    @PatchMapping(
            path = "/users/me",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> updateUser(
            @CurrentUser UserPrincipal user,
            @Valid @RequestBody UpdateUserRequest request) {

        userService.updateUserProfile(user.getId(), request);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/users/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserProfileResponse>> userProfile(
            @PathVariable("id") String id) {

        UserProfileResponse userProfile = userService.getUserProfile(id);

        WebResponse<UserProfileResponse> response = WebResponse.<UserProfileResponse>builder()
                .data(userProfile)
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @PatchMapping(
            path = "/users/me/points",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> addPointsToCurrentUser(
            @CurrentUser UserPrincipal user,
            @RequestBody Map<String, Integer> request) {

        int pointsToAdd = request.get("points");

        userService.addPoints(user.getId(), pointsToAdd);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("Points added successfully")
                .build();

        return ResponseEntity.ok(response);
    }

}
