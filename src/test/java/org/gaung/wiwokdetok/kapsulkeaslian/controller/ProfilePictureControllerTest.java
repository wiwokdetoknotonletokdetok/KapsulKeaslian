package org.gaung.wiwokdetok.kapsulkeaslian.controller;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfilePictureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

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
            int firstSlash = profilePictureUrl.indexOf('/');
            int secondSlash = profilePictureUrl.indexOf('/', firstSlash + 1);
            int thirdSlash = profilePictureUrl.indexOf('/', secondSlash + 1);
            String fileName = profilePictureUrl.substring(thirdSlash + 1);
            assertEquals("users/default.jpg", fileName);
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
