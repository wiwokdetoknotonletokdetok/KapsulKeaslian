package org.gaung.wiwokdetok.kapsulkeaslian.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_USER_POINTS = "user.points";
    public static final String ROUTING_KEY_USER_POINTS = "user.points";
    public static final String EXCHANGE_NAME = "book.exchange";

    @Bean
    public Queue userPointsQueue() {
        return new Queue(QUEUE_USER_POINTS);
    }

    @Bean
    public Binding userPointsBinding(Queue userPointsQueue) {
        return BindingBuilder.bind(userPointsQueue).to(new TopicExchange(EXCHANGE_NAME)).with(ROUTING_KEY_USER_POINTS);
    }
}
