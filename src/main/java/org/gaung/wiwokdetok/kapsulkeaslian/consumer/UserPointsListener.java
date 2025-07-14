package org.gaung.wiwokdetok.kapsulkeaslian.consumer;

import org.gaung.wiwokdetok.kapsulkeaslian.config.RabbitMQConfig;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.AmqpUserPointsMessage;
import org.gaung.wiwokdetok.kapsulkeaslian.service.PointService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserPointsListener {

    private final PointService pointService;

    public UserPointsListener(PointService pointService) {
        this.pointService = pointService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_USER_POINTS)
    public void receiveUserPointsMessage(AmqpUserPointsMessage message) {
        pointService.addPoints(message.getUserId(), message.getPoints());
    }
}
