package org.gaung.wiwokdetok.kapsulkeaslian.dto;

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
public class UpdatePasswordRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 8, max = 72, message = "Password harus antara 8 hingga 72 karakter")
    private String newPassword;

    @NotBlank
    @Size(min = 8, max = 72, message = "Password harus antara 8 hingga 72 karakter")
    private String confirmNewPassword;
}
