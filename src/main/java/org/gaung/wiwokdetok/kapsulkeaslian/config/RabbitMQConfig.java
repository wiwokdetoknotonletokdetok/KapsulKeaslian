package org.gaung.wiwokdetok.kapsulkeaslian.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_BOOK_ADDED = "book.added";

    public static final String QUEUE_BOOK_REVIEW_ADDED = "book.review.added";

    public static final String QUEUE_BOOK_LOCATION_ADDED = "book.location.added";

    private Queue createQueue(String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue bookAddedQueue() {
        return createQueue(QUEUE_BOOK_ADDED);
    }

    @Bean
    public Queue bookReviewAddedQueue() {
        return createQueue(QUEUE_BOOK_REVIEW_ADDED);
    }

    @Bean
    public Queue bookLocationAddedQueue() {
        return createQueue(QUEUE_BOOK_LOCATION_ADDED);
    }
}
