package org.gaung.wiwokdetok.kapsulkeaslian.listener;

import org.gaung.wiwokdetok.kapsulkeaslian.config.RabbitMQConfig;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.AmqpBookLocationMessage;
import org.gaung.wiwokdetok.kapsulkeaslian.service.PointService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BookLocationListener {

    private final PointService pointService;

    public BookLocationListener(PointService pointService) {
        this.pointService = pointService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_BOOK_LOCATION_ADDED)
    public void listenBookLocationAddedPoints(AmqpBookLocationMessage message) {
        pointService.addPoints(message.getCreatedBy(), 1);
    }
}
