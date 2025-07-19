package org.gaung.wiwokdetok.kapsulkeaslian.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "wiwokdetok.exchange";

    public static final String QUEUE_USER_POINTS = "kapsulkeaslian.user.points.queue";

    public static final String QUEUE_USER_ACTIVITY = "pustakacerdas.user.activity.queue";

    public static final String ROUTING_KEY_USER_ACTIVITY_REGISTERED = "user.activity.registered";

    private Queue createQueue(String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange exchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    public Queue bookQueue() {
        return createQueue(QUEUE_USER_POINTS);
    }

    @Bean
    public Queue userActivityQueue() {
        return createQueue(QUEUE_USER_ACTIVITY);
    }

    @Bean
    public Binding bindingUserRegistered(Queue userActivityQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userActivityQueue).to(exchange).with(ROUTING_KEY_USER_ACTIVITY_REGISTERED);
    }
}
