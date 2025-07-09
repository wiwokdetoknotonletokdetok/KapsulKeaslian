package org.gaung.wiwokdetok.kapsulkeaslian.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_BOOK_ADDED = "book.added";

    public static final String QUEUE_REVIEW_ADDED = "review.added";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue bookAddedQueue() {
        return new Queue(QUEUE_BOOK_ADDED, true);
    }

    @Bean
    public Queue reviewAddedQueue() {
        return new Queue(QUEUE_REVIEW_ADDED, true);
    }
}
