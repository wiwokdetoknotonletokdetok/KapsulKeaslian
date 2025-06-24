package org.wiwokdetok.kapsulkeaslian.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wiwokdetok.kapsulkeaslian.validation.AtLeastOneFieldNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AtLeastOneFieldNotBlank
public class UpdateUserRequest {

    @Size(max = 254, message = "Email maksimum 254 karakter")
    private String email;

    @Size(max = 150, message = "Nama maksimum 150 karakter")
    private String name;
}
