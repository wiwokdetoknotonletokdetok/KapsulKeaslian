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
    @Size(max = 72, message = "Password maksimum 72 karakter")
    private String newPassword;

    @NotBlank
    @Size(max = 72, message = "Password maksimum 72 karakter")
    private String confirmNewPassword;
}
