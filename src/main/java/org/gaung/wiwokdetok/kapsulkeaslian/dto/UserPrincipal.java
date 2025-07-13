package org.gaung.wiwokdetok.kapsulkeaslian.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserPrincipal {

    private UUID id;

    private String role;
}
