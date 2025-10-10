package com.cosmacare.repair_service.producer;

import com.cosmacare.repair_service.entity.RepairCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RepairEventProducer {

    private static final String TOPIC = "repair-created";
    private final KafkaTemplate<String, RepairCreatedEvent> kafkaTemplate;

    public RepairEventProducer(KafkaTemplate<String, RepairCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRepairCreatedEvent(RepairCreatedEvent event) {
        String key = event.getId(); // ✅ Use repair ID as key for partition consistency
        ProducerRecord<String, RepairCreatedEvent> record =
                new ProducerRecord<>(TOPIC, key, event);

        log.info("🚀 Sending RepairCreatedEvent to Kafka | Topic: {}, Key: {}, Event: {}", TOPIC, key, event);

        kafkaTemplate.send(record).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Successfully sent RepairCreatedEvent with key={} to partition={}, offset={}",
                        key,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("❌ Failed to send RepairCreatedEvent with key={} due to {}", key, ex.getMessage(), ex);
            }
        });
    }
}
