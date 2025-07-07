package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProfilePictureService {

    String uploadProfilePicture(UUID userId, MultipartFile profilePicture);

    void deleteProfilePicture(UUID userId);
}
