package org.gaung.wiwokdetok.kapsulkeaslian.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gaung.wiwokdetok.kapsulkeaslian.validation.ImageMinSize;
import org.gaung.wiwokdetok.kapsulkeaslian.validation.ValidImageMimeType;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfilePictureRequest {

    @NotNull(message = "Silakan unggah foto profil terlebih dahulu.")
    @ValidImageMimeType(message = "Ukuran gambar terlalu kecil. Minimal 320x320 piksel.")
    @ImageMinSize(
            minWidth = 320,
            minHeight = 320,
            message = "Format gambar tidak didukung. Gunakan JPG, PNG, atau WEBP."
    )
    private MultipartFile profilePicture;
}
