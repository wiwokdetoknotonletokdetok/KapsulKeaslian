package org.wiwokdetok.kapsulkeaslian.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.LoginUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.LoginUserResponse;
import org.wiwokdetok.kapsulkeaslian.model.RegisterUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UpdatePasswordRequest;
import org.wiwokdetok.kapsulkeaslian.model.WebResponse;
import org.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;
import org.wiwokdetok.kapsulkeaslian.service.AuthenticationService;

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

        User user = authenticationService.authenticate(request.getEmail(), request.getPassword());

        String token = authenticationService.generateToken(user);

        LoginUserResponse loginUserResponse = new LoginUserResponse();
        loginUserResponse.setToken(token);

        WebResponse<LoginUserResponse> response = WebResponse.<LoginUserResponse>builder()
                .data(loginUserResponse)
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

        authenticationService.checkEmailExists(request.getEmail());

        authenticationService.validatePasswordConfirm(request.getPassword(), request.getConfirmPassword());

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

        User user = authenticationService.getUserFromToken(token);

        user = authenticationService.authenticate(user.getEmail(), request.getCurrentPassword());

        authenticationService.validatePasswordConfirm(request.getNewPassword(), request.getConfirmNewPassword());

        authenticationService.updatePassword(user, request.getNewPassword());

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }
}
