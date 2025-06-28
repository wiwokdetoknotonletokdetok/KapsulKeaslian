package org.gaung.wiwokdetok.kapsulkeaslian.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.gaung.wiwokdetok.kapsulkeaslian.config.TestConfig;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.LoginUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.LoginUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.RegisterUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdatePasswordRequest;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
public class AuthenticationControllerTest {

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
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        reset(jwtTokenProvider);
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setEmail(user.getEmail());
        loginUserRequest.setPassword(user.getPassword());

        mockMvc.perform(
                post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertDoesNotThrow(() -> jwtTokenProvider.decodeToken(response.getData().getToken()));
        });
    }

    @Test
    void testLoginWhenEmailNotFound() throws Exception {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setEmail("notfound@wiwokdetok.org");
        loginUserRequest.setPassword(user.getPassword());

        mockMvc.perform(
                post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testLoginWhenPasswordIsIncorrect() throws Exception {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setEmail(user.getEmail());
        loginUserRequest.setPassword("IncorrectPassword");

        mockMvc.perform(
                post("/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserRequest))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<LoginUserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setEmail("new@wiwokdetok.org");
        registerUserRequest.setPassword(user.getPassword());
        registerUserRequest.setConfirmPassword(user.getPassword());
        registerUserRequest.setName("Test User");

        mockMvc.perform(
                post("/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testRegisterWhenEmailHasTaken() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setEmail(user.getEmail());
        registerUserRequest.setPassword(user.getPassword());
        registerUserRequest.setConfirmPassword(user.getPassword());
        registerUserRequest.setName("Test User");

        mockMvc.perform(
                post("/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest))
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
    void testRegisterWhenPasswordIsNotMatch() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setEmail("new@wiwokdetok.org");
        registerUserRequest.setPassword(user.getPassword());
        registerUserRequest.setConfirmPassword(user.getPassword() + "1");
        registerUserRequest.setName("Test User");

        mockMvc.perform(
                post("/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerUserRequest))
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
    void testLogoutSuccess() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                post("/auth/logout")
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
    void testLogoutFailedWhenMethodIsNotAllowed() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        mockMvc.perform(
                patch("/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid.token.here")
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
    void testLogoutWhenTokenIsNull() throws Exception {
        mockMvc.perform(
                post("/auth/logout")
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
    void testLogoutWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(
                post("/auth/logout")
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
    void testUpdatePasswordSuccess() throws Exception {
        String token = "valid.token.here";
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken(token)).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword(user.getPassword());
        updatePasswordRequest.setNewPassword("new" + user.getPassword());
        updatePasswordRequest.setConfirmNewPassword("new" + user.getPassword());

        mockMvc.perform(
                patch("/auth/password")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest))
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
    void testUpdatePasswordFailedWhenTokenIsNull() throws Exception {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword(user.getPassword());
        updatePasswordRequest.setNewPassword("new" + user.getPassword());
        updatePasswordRequest.setConfirmNewPassword("new" + user.getPassword());

        mockMvc.perform(
                patch("/auth/password")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest))
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
    void testUpdatePasswordFailedWhenTokenIsInvalid() throws Exception {
        when(jwtTokenProvider.decodeToken("invalid.token.here"))
                .thenThrow(new JwtException("Invalid token"));

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword(user.getPassword());
        updatePasswordRequest.setNewPassword("new" + user.getPassword());
        updatePasswordRequest.setConfirmNewPassword("new" + user.getPassword());

        mockMvc.perform(
                patch("/auth/password")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest))
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
    void testUpdatePasswordWhenCurrentPasswordIsIncorrect() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword("IncorrectPassword");
        updatePasswordRequest.setNewPassword("new" + user.getPassword());
        updatePasswordRequest.setConfirmNewPassword("new" + user.getPassword());

        mockMvc.perform(
                patch("/auth/password")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest))
                        .header("Authorization", "Bearer valid.token.here")
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
    void testUpdatePasswordWhenNewPasswordIsNotMatch() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword(user.getPassword());
        updatePasswordRequest.setNewPassword("new" + user.getPassword());
        updatePasswordRequest.setConfirmNewPassword("1new" + user.getPassword());

        mockMvc.perform(
                patch("/auth/password")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest))
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
    void testUpdatePasswordFailedWhenNewPasswordIsEqualsToCurrentPassword() throws Exception {
        Claims payload = mock(Claims.class);
        when(jwtTokenProvider.decodeToken("valid.token.here")).thenReturn(payload);
        when(jwtTokenProvider.getId(payload)).thenReturn(String.valueOf(user.getId()));
        when(jwtTokenProvider.getRole(payload)).thenReturn(user.getRole());

        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword(user.getPassword());
        updatePasswordRequest.setNewPassword(user.getPassword());
        updatePasswordRequest.setConfirmNewPassword(user.getPassword());

        mockMvc.perform(
                patch("/auth/password")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest))
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
}
