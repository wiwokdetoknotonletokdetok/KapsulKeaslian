package org.gaung.wiwokdetok.kapsulkeaslian.listener;

import org.gaung.wiwokdetok.kapsulkeaslian.config.RabbitMQConfig;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.NewBookMessage;
import org.gaung.wiwokdetok.kapsulkeaslian.service.PointService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BookListener {

    private final PointService pointService;

    public BookListener(PointService pointService) {
        this.pointService = pointService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOK_ADDED)
    public void listenUserPoints(NewBookMessage message) {
        pointService.addPoints(message.getCreatedBy(), 3);
    }
}
