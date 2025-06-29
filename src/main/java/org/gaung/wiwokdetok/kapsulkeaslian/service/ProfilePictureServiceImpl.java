package org.gaung.wiwokdetok.kapsulkeaslian.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ProfilePictureServiceImpl implements ProfilePictureService {

    @Value("${CLOUDFLARE_R2_BUCKET_NAME}")
    private String bucketName;

    @Value("${CLOUDFLARE_R2_PUBLIC_ENDPOINT}")
    private String publicEndpoint;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public String uploadProfilePicture(String userId, MultipartFile file) {
        String fileName = userId + ".jpg";
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gagal upload profile picture");
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

    private void saveUserProfilePicture(String id, String profilePictureUrl) {
        User user = userService.getUserById(id);

        user.setProfilePicture(profilePictureUrl);
        userRepository.save(user);
    }

    @Override
    public void deleteProfilePicture(String id) {
        User user = userService.getUserById(id);

        user.setProfilePicture(publicEndpoint + "/default.jpg");
        userRepository.save(user);
    }
}
