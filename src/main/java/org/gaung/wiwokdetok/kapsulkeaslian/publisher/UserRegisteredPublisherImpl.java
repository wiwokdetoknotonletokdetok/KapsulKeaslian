package org.gaung.wiwokdetok.kapsulkeaslian.publisher;

import org.gaung.wiwokdetok.kapsulkeaslian.config.RabbitMQConfig;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.AmqpUserRegisteredMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserRegisteredPublisherImpl implements UserRegisteredPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserRegisteredPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendUserRegisteredMessage(AmqpUserRegisteredMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_USER_REGISTERED,
                message
        );
    }
}
