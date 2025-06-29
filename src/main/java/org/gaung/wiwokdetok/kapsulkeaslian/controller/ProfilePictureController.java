package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import jakarta.validation.Valid;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.ProfilePictureRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserPrincipal;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.WebResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.kapsulkeaslian.service.ProfilePictureService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class ProfilePictureController {

    private final ProfilePictureService profilePictureService;

    public ProfilePictureController(ProfilePictureService profilePictureService) {
        this.profilePictureService = profilePictureService;
    }

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/users/me/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> uploadProfilePicture(
            @CurrentUser UserPrincipal user,
            @Valid @ModelAttribute ProfilePictureRequest request) {

        String profilePictureUrl = profilePictureService.uploadProfilePicture(
                user.getId(), request.getProfilePicture());

        WebResponse<String> response = WebResponse.<String>builder()
                .data("Created")
                .build();

        return ResponseEntity.created(URI.create(profilePictureUrl)).body(response);
    }

    @AllowedRoles({"USER"})
    @DeleteMapping(
            path = "/users/me/profile-picture",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> deleteProfilePicture(
            @CurrentUser UserPrincipal user) {

        profilePictureService.deleteProfilePicture(user.getId());

        WebResponse<String> response = WebResponse.<String>builder()
                .data("OK")
                .build();

        return ResponseEntity.ok(response);
    }
}
