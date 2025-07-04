package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.PageInfo;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserPrincipal;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.WebResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.kapsulkeaslian.service.FollowService;
import org.gaung.wiwokdetok.kapsulkeaslian.validation.PaginationValidator;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    private final PaginationValidator paginationValidator;

    @AllowedRoles({"USER"})
    @PostMapping(
            path = "/users/{id}/follow",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> followUser(
            @CurrentUser UserPrincipal user,
            @PathVariable("id") String id) {

        followService.followUser(user.getId(), id);

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
            @CurrentUser UserPrincipal user,
            @PathVariable("id") String id) {

        followService.unfollowUser(user.getId(), id);

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
            @PathVariable("id") String id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        paginationValidator.validatePageAndSizeNumber(page, size);

        Page<SimpleUserResponse> followersPage = followService.getUserFollowers(id, page, size);

        paginationValidator.validatePageBounds(page, followersPage);

        return getPaginationResponse(followersPage);
    }

    @AllowedRoles({"USER"})
    @GetMapping(
            path = "/users/{id}/followings",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<SimpleUserResponse>>> userFollowings(
            @PathVariable("id") String id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        paginationValidator.validatePageAndSizeNumber(page, size);

        Page<SimpleUserResponse> followingsPage = followService.getUserFollowings(id, page, size);

        paginationValidator.validatePageBounds(page, followingsPage);

        return getPaginationResponse(followingsPage);
    }

    private ResponseEntity<WebResponse<List<SimpleUserResponse>>> getPaginationResponse(Page<SimpleUserResponse> followingsPage) {
        PageInfo pageInfo = PageInfo.builder()
                .size(followingsPage.getSize())
                .currentPage(processCurrentPage(followingsPage.getNumber()))
                .totalPages(followingsPage.getTotalPages())
                .totalElements(followingsPage.getTotalElements())
                .build();

        WebResponse<List<SimpleUserResponse>> response = WebResponse.<List<SimpleUserResponse>>builder()
                .data(followingsPage.getContent())
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok(response);
    }

    private int processCurrentPage(int currentPage) {
        return currentPage + 1;
    }
}
