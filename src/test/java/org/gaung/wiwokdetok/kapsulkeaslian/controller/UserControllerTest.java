package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdateUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserProfileResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.WebResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.gaung.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        String password = "password";

        user = new User();
        user.setEmail("test@wiwokdetok.org");
        user.setPassword(passwordEncoder.encode(password));
        user.setName("Test User");
        user.setBio("Hello World");
        user.setRole("USER");
        user.setProfilePicture("http://example.com");
        userRepository.save(user);
        user.setPassword(password);

        User user2 = new User();
        user2.setEmail("notavailable@wiwokdetok.org");
        user2.setPassword(passwordEncoder.encode(password));
        user2.setName("Test User 2");
        user2.setBio("Hello World 2");
        user2.setRole("USER");
        user2.setProfilePicture("http://example.com");
        userRepository.save(user2);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testUpdateUserEmailSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("updated@wiwokdetok.org");

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());

            Optional<User> updatedUser = userRepository.findById(user.getId());
            assertTrue(updatedUser.isPresent());
            assertEquals("updated@wiwokdetok.org", updatedUser.get().getEmail());
        });
    }

    @Test
    void testUpdateUserBioSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setBio("Updated");

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());

            Optional<User> updatedUser = userRepository.findById(user.getId());
            assertTrue(updatedUser.isPresent());
            assertEquals("Updated", updatedUser.get().getBio());
        });
    }

    @Test
    void testUpdateUserNameSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated Test User");

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());

            Optional<User> updatedUser = userRepository.findById(user.getId());
            assertTrue(updatedUser.isPresent());
            assertEquals("Updated Test User", updatedUser.get().getName());
        });
    }

    @Test
    void testUpdateUserWhenEmailIsTaken() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("notavailable@wiwokdetok.org");

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUpdateUserWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("notavailable@wiwokdetok.org");

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
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
    void testUpdateUserWhenTokenIsNull() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("notavailable@wiwokdetok.org");

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
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
    void testUpdateUserWhenMethodIsInvalid() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("notavailable@wiwokdetok.org");

        mockMvc.perform(
                post("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
        ).andExpectAll(
                status().isMethodNotAllowed()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetUserProfileSuccess() throws Exception {
        mockMvc.perform(
                get("/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetUserProfileSuccessWhenUserIsAuthenticated() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                get("/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetUserProfileSuccessWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                get("/users/" + user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid.token.here")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetUserNotFound() throws Exception {
        mockMvc.perform(
                get("/users/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<UserProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetUserNotFoundWhenUserIsAuthenticated() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                get("/users/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<UserProfileResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }
}
