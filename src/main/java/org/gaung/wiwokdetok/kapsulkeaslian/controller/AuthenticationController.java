package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import jakarta.validation.Valid;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.LoginUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.LoginUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.RegisterUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdatePasswordRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.WebResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.kapsulkeaslian.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(
            path = "/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<LoginUserResponse>> login(
            @Valid @RequestBody LoginUserRequest request) {

        LoginUserResponse userToken = authenticationService.authenticate(request.getEmail(), request.getPassword());

        WebResponse<LoginUserResponse> response = WebResponse.<LoginUserResponse>builder()
                .data(userToken)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping(
            path = "/auth/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> register(
            @Valid @RequestBody RegisterUserRequest request) {

        authenticationService.registerUser(request);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> logout() {

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }

    @AllowedRoles({"USER"})
    @PatchMapping(
            path = "/auth/password",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> updatePassword(
            @RequestHeader(name = "Authorization", required = false) String token,
            @Valid @RequestBody UpdatePasswordRequest request) {

        authenticationService.updateUserPassword(token, request);

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }
}
