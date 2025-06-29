package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.SimpleUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.WebResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.model.Follow;
import org.gaung.wiwokdetok.kapsulkeaslian.model.User;
import org.gaung.wiwokdetok.kapsulkeaslian.repository.FollowRepository;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    private User user2;

    @BeforeEach
    void setUp() {
        String password = "password";

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@wiwokdetok.org");
        user.setPassword(passwordEncoder.encode(password));
        user.setName("Test User");
        user.setBio("Hello World");
        user.setRole("USER");
        user.setProfilePicture("http://example.com");
        userRepository.save(user);

        user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("user2@wiwokdetok.org");
        user2.setPassword(passwordEncoder.encode(password));
        user2.setName("Test User 2");
        user2.setBio("Hello World 2");
        user2.setRole("USER");
        user2.setProfilePicture("http://example.com");
        userRepository.save(user2);
    }

    @AfterEach
    void tearDown() {
        followRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFollowSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                post("/users/{id}/follow", user2.getId())
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
        });
    }

    @Test
    void testFollowFailedWhenTokenIsNull() throws Exception {
        mockMvc.perform(
                post("/users/{id}/follow", user2.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testFollowFailedWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                post("/users/{id}/follow", user2.getId())
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

    @Test
    void testFollowFailedWhenFollowHimSelf() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                post("/users/{id}/follow", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testFollowFailedWhenTargetUserIsAlreadyFollowed() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        Follow follow = new Follow(user, user2);
        followRepository.save(follow);

        mockMvc.perform(
                post("/users/{id}/follow", user2.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testFollowFailedWhenTargetUserIsNotFound() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                post("/users/{id}/follow", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testUnfollowSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        Follow follow = new Follow(user, user2);
        followRepository.save(follow);

        mockMvc.perform(
                delete("/users/{id}/follow", user2.getId())
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
        });
    }

    @Test
    void testUnfollowFailedWhenTokenIsNull() throws Exception {
        Follow follow = new Follow(user, user2);
        followRepository.save(follow);

        mockMvc.perform(
                delete("/users/{id}/follow", user2.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testUnfollowFailedWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        Follow follow = new Follow(user, user2);
        followRepository.save(follow);

        mockMvc.perform(
                delete("/users/{id}/follow", user2.getId())
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

    @Test
    void testUnfollowFailedWhenTargetUserNotFollowed() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                delete("/users/{id}/follow", user2.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testUnfollowFailedWhenTargetUserIsHimSelf() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                delete("/users/{id}/follow", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testGetCurrentUserFollowersFailedWhenTokenIsNull() throws Exception {
        mockMvc.perform(
                get("/users/{id}/followers", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<List<SimpleUserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetCurrentUserFollowersFailedWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                get("/users/{id}/followers", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid.token.here")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<List<SimpleUserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetCurrentUserFollowingsFailedWhenTokenIsNull() throws Exception {
        mockMvc.perform(
                get("/users/{id}/followings", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<List<SimpleUserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetCurrentUserFollowingsFailedWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                get("/users/{id}/followings", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid.token.here")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<List<SimpleUserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testCurrentUserFollowersCount() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        Follow follow = new Follow(user, user2);
        followRepository.save(follow);

        mockMvc.perform(
                get("/users/{id}/followers", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SimpleUserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
        });
    }

    @Test
    void testCurrentUserFollowingsCount() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        Follow follow = new Follow(user, user2);
        followRepository.save(follow);

        mockMvc.perform(
                get("/users/{id}/followings", user.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")

        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SimpleUserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(1, response.getData().size());
        });
    }

    @Test
    void testTargetUserFollowersCount() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        Follow follow = new Follow(user, user2);
        followRepository.save(follow);

        mockMvc.perform(
                get("/users/{id}/followers", user2.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SimpleUserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(1, response.getData().size());
        });
    }

    @Test
    void testTargetUserFollowingsCount() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        Follow follow = new Follow(user, user2);
        followRepository.save(follow);

        mockMvc.perform(
                get("/users/{id}/followings", user2.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SimpleUserResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(0, response.getData().size());
        });
    }
}
