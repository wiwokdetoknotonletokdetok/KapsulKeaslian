package org.gaung.wiwokdetok.kapsulkeaslian.listener;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserPointMessage;
import org.gaung.wiwokdetok.kapsulkeaslian.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookListenerImpl implements BookListener {

    private final UserService userService;

    @Override
    @RabbitListener(queues = "user.points")
    public void listenUserPoints(UserPointMessage message) {
        userService.addPoints(message.getUserId(), message.getPoints());
    }
}
