package org.wiwokdetok.kapsulkeaslian.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.UpdateUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UserProfileResponse;
import org.wiwokdetok.kapsulkeaslian.model.WebResponse;
import org.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;
import org.wiwokdetok.kapsulkeaslian.service.AuthenticationService;
import org.wiwokdetok.kapsulkeaslian.service.UserService;

@RestController
public class UserController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @AllowedRoles({"USER"})
    @PatchMapping(
            path = "/users/me",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> updateUser(
            @RequestHeader(name = "Authorization", required = false) String token,
            @Valid @RequestBody UpdateUserRequest request) {

        User user = authenticationService.getUserFromToken(token);

        userService.validateEmailChange(user, request.getEmail());

        userService.updateUserData(user, request);

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

        User user = userService.getUserById(id);

        UserProfileResponse userProfile = userService.mapToUserProfileResponse(user);

        WebResponse<UserProfileResponse> response = WebResponse.<UserProfileResponse>builder()
                .data(userProfile)
                .build();

        return ResponseEntity.ok(response);
    }
}
