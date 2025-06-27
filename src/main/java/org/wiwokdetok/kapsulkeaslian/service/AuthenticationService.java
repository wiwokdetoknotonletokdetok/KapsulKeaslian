package org.wiwokdetok.kapsulkeaslian.service;

import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.LoginUserResponse;
import org.wiwokdetok.kapsulkeaslian.model.RegisterUserRequest;
import org.wiwokdetok.kapsulkeaslian.model.UpdatePasswordRequest;

public interface AuthenticationService {

    LoginUserResponse authenticate(String username, String password);

    void registerUser(RegisterUserRequest request);

    void updateUserPassword(String token, UpdatePasswordRequest request);

    User getUserFromToken(String token);
}
