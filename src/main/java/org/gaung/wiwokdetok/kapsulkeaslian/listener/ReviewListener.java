package org.gaung.wiwokdetok.kapsulkeaslian.listener;

import org.gaung.wiwokdetok.kapsulkeaslian.config.RabbitMQConfig;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.NewReviewMessage;
import org.gaung.wiwokdetok.kapsulkeaslian.service.PointService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ReviewListener {

    private final PointService pointService;

    public ReviewListener(PointService pointService) {
        this.pointService = pointService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_REVIEW_ADDED)
    public void listenReviewPoints(NewReviewMessage message) {
        pointService.addPoints(message.getCreatedBy(), 1);
    }
}
