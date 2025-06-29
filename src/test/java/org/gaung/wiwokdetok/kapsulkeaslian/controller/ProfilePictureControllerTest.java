package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.WebResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.gaung.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;
import org.gaung.wiwokdetok.kapsulkeaslian.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfilePictureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AmazonS3 amazonS3;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        String password = "password";

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@wiwokdetok.org");
        user.setPassword(passwordEncoder.encode(password));
        user.setName("Test User");
        user.setBio("Hello World");
        user.setRole("USER");
        user.setProfilePicture("http://example.com");
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testUploadProfilePictureSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        BufferedImage dummyImage = new BufferedImage(320, 320, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "jpg", os);
        byte[] imageBytes = os.toByteArray();

        MockMultipartFile multipartFile = new MockMultipartFile(
                "profilePicture",
                "dummy.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                imageBytes
        );

        when(amazonS3.putObject(any(PutObjectRequest.class)))
                .thenReturn(new PutObjectResult());

        mockMvc.perform(
                multipart("/users/me/profile-picture")
                        .file(multipartFile)
                        .header("Authorization", "Bearer valid.token.here")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {}
            );
            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testUploadProfilePictureFailedWhenImageIsTooSmall() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        BufferedImage dummyImage = new BufferedImage(100, 320, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(dummyImage, "jpg", os);
        byte[] imageBytes = os.toByteArray();

        MockMultipartFile multipartFile = new MockMultipartFile(
                "profilePicture",
                "dummy.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                imageBytes
        );

        when(amazonS3.putObject(any(PutObjectRequest.class)))
                .thenReturn(new PutObjectResult());

        mockMvc.perform(
                multipart("/users/me/profile-picture")
                        .file(multipartFile)
                        .header("Authorization", "Bearer valid.token.here")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {}
            );
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUploadProfilePictureFailedWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                post("/users/me/profile-picture")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .header("Authorization", "Bearer invalid.token.here")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testDeleteProfilePictureSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                delete("/users/me/profile-picture")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());

            User updatedUser = userService.getUserById(String.valueOf(user.getId()));

            String profilePictureUrl = updatedUser.getProfilePicture();
            assertTrue(profilePictureUrl.contains("/users/default.jpg"));
        });
    }

    @Test
    void testDeleteProfilePictureFailedWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                delete("/users/me/profile-picture")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid.token.here")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }
}
