package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import jakarta.validation.Valid;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.PageInfo;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdateUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserPrincipal;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserProfileResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserRankingResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.WebResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.AllowedRoles;
import org.gaung.wiwokdetok.kapsulkeaslian.security.annotation.CurrentUser;
import org.gaung.wiwokdetok.kapsulkeaslian.service.PointService;
import org.gaung.wiwokdetok.kapsulkeaslian.service.UserService;
import org.gaung.wiwokdetok.kapsulkeaslian.validation.PaginationValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;

    private final PointService pointService;

    private final PaginationValidator paginationValidator;

    public UserController(UserService userService, PointService pointService, PaginationValidator paginationValidator) {
        this.userService = userService;
        this.pointService = pointService;
        this.paginationValidator = paginationValidator;
    }

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
            path = "/users/{userId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<UserProfileResponse>> userProfile(
            @PathVariable("userId") UUID userId) {

        UserProfileResponse userProfile = userService.getUserProfile(userId);

        WebResponse<UserProfileResponse> response = WebResponse.<UserProfileResponse>builder()
                .data(userProfile)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            path = "/rank",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<List<UserRankingResponse>>> getUserRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        paginationValidator.validatePageAndSizeNumber(page, size);

        List<UserRankingResponse> allRankings = pointService.getUserRanking();

        int totalElements = allRankings.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        if (totalPages > 0 && page > totalPages) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page melebihi jumlah halaman yang tersedia");
        }

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<UserRankingResponse> pagedRankings = allRankings.subList(fromIndex, toIndex);

        PageInfo pageInfo = PageInfo.builder()
                .size(size)
                .currentPage(page)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();

        WebResponse<List<UserRankingResponse>> response = WebResponse.<List<UserRankingResponse>>builder()
                .data(pagedRankings)
                .pageInfo(pageInfo)
                .build();

        return ResponseEntity.ok(response);
    }
}
