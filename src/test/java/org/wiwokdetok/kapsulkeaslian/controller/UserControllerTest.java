package org.wiwokdetok.kapsulkeaslian.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.LoginUserResponse;
import org.wiwokdetok.kapsulkeaslian.model.UpdateUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.WebResponse;
import org.wiwokdetok.kapsulkeaslian.repository.UserRepository;
import org.wiwokdetok.kapsulkeaslian.security.JwtTokenProvider;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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
        user.setPassword(password);

        User user2 = new User();
        user2.setId(UUID.randomUUID());
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
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("updated@wiwokdetok.org");

        String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()), user.getRole());

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
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
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setBio("Updated");

        String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()), user.getRole());

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
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
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Updated Test User");

        String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()), user.getRole());

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
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
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setEmail("notavailable@wiwokdetok.org");

        String token = jwtTokenProvider.generateToken(String.valueOf(user.getId()), user.getRole());

        mockMvc.perform(
                patch("/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .header("Authorization", "Bearer " + token)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());;
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
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());;
        });
    }

    @Test
    void testGetUserProfileWhenUserIsNotFound() throws Exception {
        mockMvc.perform(
                get("/users/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());;
        });
    }
}
