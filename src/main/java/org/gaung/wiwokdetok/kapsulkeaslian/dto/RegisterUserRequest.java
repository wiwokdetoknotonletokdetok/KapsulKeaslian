package org.gaung.wiwokdetok.kapsulkeaslian.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaung.wiwokdetok.kapsulkeaslian.validation.PasswordsMatch;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordsMatch
public class RegisterUserRequest implements PasswordConfirmation {

    @NotBlank(message = "Email tidak boleh kosong.")
    @Size(max = 254, message = "Email tidak boleh lebih dari 254 karakter.")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Format email tidak valid."
    )
    private String email;

    @NotBlank(message = "Kata sandi tidak boleh kosong.")
    @Size(
            min = 8,
            max = 72,
            message = "Kata sandi harus antara 8 hingga 72 karakter."
    )
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%^&+=!_-]+$",
            message = "Kata sandi hanya boleh berisi huruf, angka, dan simbol @#$%^&+=!_- dengan minimal satu huruf dan satu angka."
    )
    private String password;

    @NotBlank(message = "Konfirmasi kata sandi tidak boleh kosong.")
    @Size(
            min = 8,
            max = 72,
            message = "Konfirmasi kata sandi harus antara 8 hingga 72 karakter."
    )
    private String confirmPassword;

    @NotBlank(message = "Nama tidak boleh kosong.")
    @Size(max = 50, message = "Nama tidak boleh lebih dari 50 karakter.")
    private String name;
}
