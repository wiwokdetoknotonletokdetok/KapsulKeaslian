package org.gaung.wiwokdetok.kapsulkeaslian.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmqpUserPointsMessage {

    private UUID userId;

    private int points;
}
