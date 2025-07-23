package org.gaung.wiwokdetok.kapsulkeaslian.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRankingResponse {

    private UUID id;
    private String profilePicture;
    private String name;
    private int points;
}

