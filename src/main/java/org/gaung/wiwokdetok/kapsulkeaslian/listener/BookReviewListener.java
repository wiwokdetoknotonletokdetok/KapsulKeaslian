package org.gaung.wiwokdetok.kapsulkeaslian.listener;

import org.gaung.wiwokdetok.kapsulkeaslian.config.RabbitMQConfig;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.AmqpBookReviewMessage;
import org.gaung.wiwokdetok.kapsulkeaslian.service.PointService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BookReviewListener {

    private final PointService pointService;

    public BookReviewListener(PointService pointService) {
        this.pointService = pointService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOK_REVIEW_ADDED)
    public void listenBookReviewAddedPoints(AmqpBookReviewMessage message) {
        pointService.addPoints(message.getCreatedBy(), 1);
    }
}
