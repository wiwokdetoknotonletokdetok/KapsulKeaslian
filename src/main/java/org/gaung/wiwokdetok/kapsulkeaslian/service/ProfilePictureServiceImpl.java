package org.gaung.wiwokdetok.kapsulkeaslian.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfilePictureServiceImpl implements ProfilePictureService {

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.public-endpoint}")
    private String publicEndpoint;

    private final UserRepository userRepository;

    private final UserService userService;

    private final AmazonS3 amazonS3;

    @Override
    public String uploadProfilePicture(UUID userId, MultipartFile file) {
        String fileName = String.format("users/%s.jpg", userId);
        String version = String.valueOf(System.currentTimeMillis());

        try {
            ByteArrayInputStream resizedImageStream = resizeAndCropImage(file);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(resizedImageStream.available());
            metadata.setContentType("image/jpeg");

            amazonS3.putObject(
                    new PutObjectRequest(bucketName, fileName, resizedImageStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );

            String profilePictureUrl = String.format("%s/%s?v=%s", publicEndpoint, fileName, version);

            saveUserProfilePicture(userId, profilePictureUrl);

            return profilePictureUrl;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Terjadi kesalahan saat memproses file gambar.");
        }
    }

    private ByteArrayInputStream resizeAndCropImage(MultipartFile file) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(file.getInputStream())
                    .crop(Positions.CENTER)
                    .size(320, 320)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    private void saveUserProfilePicture(UUID userId, String profilePictureUrl) {
        User user = userService.getUserById(userId);

        user.setProfilePicture(profilePictureUrl);
        userRepository.save(user);
    }

    @Override
    public void deleteProfilePicture(UUID userId) {
        User user = userService.getUserById(userId);

        user.setProfilePicture(publicEndpoint + "/users/default.jpg");
        userRepository.save(user);
    }
}
