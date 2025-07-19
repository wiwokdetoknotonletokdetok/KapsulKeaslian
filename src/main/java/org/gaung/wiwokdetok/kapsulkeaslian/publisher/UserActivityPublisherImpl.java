package org.gaung.wiwokdetok.kapsulkeaslian.publisher;

import org.gaung.wiwokdetok.kapsulkeaslian.config.RabbitMQConfig;
import org.gaung.wiwokdetok.kapsulkeaslian.dto.AmqpUserRegisteredMessage;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserActivityPublisherImpl implements UserActivityPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserActivityPublisherImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendUserRegisteredMessage(AmqpUserRegisteredMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_USER_ACTIVITY_REGISTERED,
                message, msg -> {
                    msg.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return msg;
                });
    }
}
