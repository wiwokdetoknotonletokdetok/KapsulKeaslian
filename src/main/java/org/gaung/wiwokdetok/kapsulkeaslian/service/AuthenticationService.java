package org.gaung.wiwokdetok.kapsulkeaslian.service;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.LoginUserResponse;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.RegisterUserRequest;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UpdatePasswordRequest;

import java.util.UUID;

public interface AuthenticationService {

    LoginUserResponse authenticate(String username, String password);

    void registerUser(RegisterUserRequest request);

    void updateUserPassword(UUID userId, UpdatePasswordRequest request);
}
