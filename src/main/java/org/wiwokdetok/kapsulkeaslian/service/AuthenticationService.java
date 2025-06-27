package org.wiwokdetok.kapsulkeaslian.service;

import org.wiwokdetok.kapsulkeaslian.entity.User;
import org.wiwokdetok.kapsulkeaslian.model.RegisterUserRequest;

public interface AuthenticationService {

    User authenticate(String username, String password);

    void validatePasswordConfirm(String password, String confirmPassword);

    void checkEmailExists(String email);

    void registerUser(RegisterUserRequest request);

    User getUserFromToken(String token);

    void updatePassword(User user, String newPassword);

    String generateToken(User user);

    void validateNewPassword(String currentPassword, String newPassword);
}
