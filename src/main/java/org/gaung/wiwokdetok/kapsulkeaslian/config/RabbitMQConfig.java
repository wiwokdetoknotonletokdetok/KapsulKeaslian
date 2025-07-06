package org.gaung.wiwokdetok.kapsulkeaslian.config;

import org.gaung.wiwokdetok.kapsulkeaslian.dto.UserPointMessage;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_USER_POINTS = "user.points";
    public static final String ROUTING_KEY_USER_POINTS = "user.points";
    public static final String EXCHANGE_NAME = "book.exchange";
    public static final String USER_POINT_MESSAGE_TYPE_ID = "org.gaung.wiwokdetok.fondasikehidupan.dto.UserPointMessage";

    @Bean
    public Jackson2JsonMessageConverter consumerJackson2MessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put(USER_POINT_MESSAGE_TYPE_ID, UserPointMessage.class);
        converter.setClassMapper(new DefaultClassMapper() {{
            setIdClassMapping(typeIdMappings);
        }});

        return converter;
    }

    @Bean
    public TopicExchange bookExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue userPointsQueue() {
        return new Queue(QUEUE_USER_POINTS, true);
    }

    @Bean
    public Binding userPointsBinding(Queue userPointsQueue, TopicExchange bookExchange) {
        return BindingBuilder
                .bind(userPointsQueue)
                .to(bookExchange)
                .with(ROUTING_KEY_USER_POINTS);
    }

}
