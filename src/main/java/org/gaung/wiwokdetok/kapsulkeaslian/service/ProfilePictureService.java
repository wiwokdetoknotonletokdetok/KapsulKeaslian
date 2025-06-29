package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.springframework.web.multipart.MultipartFile;

public interface ProfilePictureService {

    String uploadProfilePicture(String id, MultipartFile profilePicture);

    void deleteProfilePicture(String id);
}
