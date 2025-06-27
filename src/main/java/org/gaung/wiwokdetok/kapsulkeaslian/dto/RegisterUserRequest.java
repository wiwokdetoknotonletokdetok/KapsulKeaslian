package org.gaung.wiwokdetok.kapsulkeaslian.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Format email tidak valid")
    private String email;

    @NotBlank
    @Size(min = 8, max = 72, message = "Password harus antara 8 hingga 72 karakter")
    private String password;

    @NotBlank
    @Size(min = 8, max = 72, message = "Password harus antara 8 hingga 72 karakter")
    private String confirmPassword;

    @NotBlank
    @Size(max = 50, message = "Nama maksimum 50 karakter")
    private String name;
}
