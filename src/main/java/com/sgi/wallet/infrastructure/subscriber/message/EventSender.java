package com.sgi.wallet.infrastructure.subscriber.message;

import com.sgi.wallet.infrastructure.mapper.ObjectMappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class EventSender {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventSender(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @SneakyThrows
    public void sendEvent(String topic, Object event) {
            String value = ObjectMappers.OBJECT_MAPPER.writeValueAsString(event);
            log.info("Publishing to Kafka topic {}: {}", topic, event);
        kafkaTemplate.send(new ProducerRecord<>(topic, value));
    }
}