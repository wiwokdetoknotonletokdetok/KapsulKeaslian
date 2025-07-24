package org.gaung.wiwokdetok.kapsulkeaslian.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserRankingResponse {

    private UUID id;

    private String email;

    private String name;

    private int points;

    private String profilePicture;
}
