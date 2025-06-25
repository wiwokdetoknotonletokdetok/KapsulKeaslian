package org.wiwokdetok.kapsulkeaslian.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private String email;

    private String name;

    private String bio;

    private String profilePicture;

    private int followers;

    private int followings;

    private int points;
}
