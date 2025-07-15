package org.gaung.wiwokdetok.kapsulkeaslian.consumer;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.AmqpUserPointsMessage;
import org.gaung.wiwokdetok.kapsulkeaslian.service.PointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserPointsConsumerTest {

    private PointService pointService;

    private UserPointsConsumer userPointsConsumer;

    private UUID userId;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        pointService = mock(PointService.class);
        userPointsConsumer = new UserPointsConsumer(pointService);
    }

    @Test
    public void testReceiveUserPointsMessage_ShouldCallAddPoints() {
        AmqpUserPointsMessage message = new AmqpUserPointsMessage();
        message.setUserId(userId);
        message.setPoints(100);

        userPointsConsumer.receiveUserPointsMessage(message);

        verify(pointService, times(1)).addPoints(userId, 100);
    }
}

