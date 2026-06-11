package com.loan_org.verification_service.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Handles RabbitMQ infrastructure topology declarations for the Verification Service.
 * This configuration is completely self-contained and isolated from other microservices.
 *
 * @author amanfoundongithub
 * @version 2.0.0
 */
@Configuration
public class RabbitMQConfig {

    // ─── STANDARD EXCHANGES & QUEUES ───
    public static final String VERIFICATION_EXCHANGE = "verification.exchange";
    public static final String INBOUND_VERIFICATION_QUEUE = "docs.uploaded.verification.queue";

    // ─── DEAD LETTER INFRASTRUCTURE ───
    public static final String VERIFICATION_DLX = "verification.dlx";
    public static final String VERIFICATION_DLQ = "docs.uploaded.verification.dlq";

    // ─── ROUTING KEYS ───
    public static final String ROUTING_DOC_UPLOADED = "document.status.uploaded";
    public static final String ROUTING_DEAD_LETTER = "verification.deadletter";

    // ─── STANDARD EXCHANGE BEAN ───
    @Bean
    public TopicExchange verificationExchange() {
        return new TopicExchange(VERIFICATION_EXCHANGE);
    }

    // ─── DEAD LETTER INFRASTRUCTURE BEANS ───
    @Bean
    public TopicExchange verificationDeadLetterExchange() {
        return new TopicExchange(VERIFICATION_DLX);
    }

    @Bean
    public Queue verificationDeadLetterQueue() {
        return QueueBuilder.durable(VERIFICATION_DLQ).build();
    }

    @Bean
    public Binding bindDeadLetterQueue() {
        return BindingBuilder.bind(verificationDeadLetterQueue())
                .to(verificationDeadLetterExchange())
                .with(ROUTING_DEAD_LETTER);
    }

    // ─── INBOUND QUEUE WITH DEAD LETTER ROUTING CONFIGURATION ───
    @Bean
    public Queue inboundVerificationQueue() {
        return QueueBuilder.durable(INBOUND_VERIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", VERIFICATION_DLX)
                .withArgument("x-dead-letter-routing-key", ROUTING_DEAD_LETTER)
                .build();
    }

    @Bean
    public Binding bindInboundQueueToVerificationExchange() {
        return BindingBuilder.bind(inboundVerificationQueue())
                .to(verificationExchange())
                .with(ROUTING_DOC_UPLOADED);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}