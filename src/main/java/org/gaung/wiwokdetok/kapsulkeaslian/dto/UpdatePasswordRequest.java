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
public class UpdatePasswordRequest {

    @NotBlank(message = "Kata sandi saat ini tidak boleh kosong.")
    private String currentPassword;

    @NotBlank(message = "Kata sandi baru tidak boleh kosong.")
    @Size(
            min = 8,
            max = 72,
            message = "Kata sandi baru harus antara 8 hingga 72 karakter."
    )
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%^&+=!_-]+$",
            message = "Kata sandi baru hanya boleh berisi huruf, angka, dan simbol @#$%^&+=!_- dengan minimal satu huruf dan satu angka."
    )
    private String newPassword;

    @NotBlank(message = "Konfirmasi kata sandi baru tidak boleh kosong.")
    @Size(
            min = 8,
            max = 72,
            message = "Konfirmasi kata sandi baru harus antara 8 hingga 72 karakter."
    )
    private String confirmNewPassword;
}
