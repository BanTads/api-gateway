package com.apigateway.orquestrador.orquestrador.connections;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.apigateway.orquestrador.orquestrador.constants.QueueConstants;

import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitMQConnection {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConnection.class);

    @Value("${rabbitmq.exchange:api-gateway-exchange}")
    private String nameExchange;

    private final AmqpAdmin amqpAdmin;

    public RabbitMQConnection(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    protected Queue createQueue(String queueName, long ttl) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", ttl);  // TTL in milliseconds
        return new Queue(queueName, true, false, false, args);
    }

    protected DirectExchange createExchange() {
        return new DirectExchange(nameExchange);
    }

    protected Binding bindQueueToExchange(Queue queue, DirectExchange exchange) {
        return new Binding(queue.getName(), DestinationType.QUEUE, exchange.getName(), queue.getName(), null);
    }

    @PostConstruct
    private void createQueues() {
        try {
            DirectExchange exchange = createExchange();
            this.amqpAdmin.declareExchange(exchange);

            String[] queueNames = QueueConstants.ALL_QUEUES;

            for (String queueName : queueNames) {
                // Remove the existing queue
                this.amqpAdmin.deleteQueue(queueName);
                // Declare the new queue with TTL
                Queue queue = this.createQueue(queueName, 10000); // TTL of 60 seconds
                Binding binding = this.bindQueueToExchange(queue, exchange);
                this.amqpAdmin.declareQueue(queue);
                this.amqpAdmin.declareBinding(binding);
            }
        } catch (Exception e) {
            logger.error("An error occurred while creating queues and bindings: {}", e.getMessage(), e);
        }
    }
}
