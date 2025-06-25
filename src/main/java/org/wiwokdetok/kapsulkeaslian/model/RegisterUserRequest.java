package org.wiwokdetok.kapsulkeaslian.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {

    @NotBlank
    @Size(max = 254, message = "Email maksimum 254 karakter")
    private String email;

    @NotBlank
    @Size(max = 72, message = "Password maksimum 72 karakter")
    private String password;

    @NotBlank
    @Size(max = 72, message = "Password maksimum 72 karakter")
    private String confirmPassword;

    @NotBlank
    @Size(max = 50, message = "Nama maksimum 50 karakter")
    private String name;
}
