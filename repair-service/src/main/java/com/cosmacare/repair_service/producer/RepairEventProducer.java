package com.cosmacare.repair_service.producer;

import com.cosmacare.repair_service.entity.RepairCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RepairEventProducer {

    private final KafkaTemplate<String, RepairCreatedEvent> kafkaTemplate;

    public RepairEventProducer(KafkaTemplate<String, RepairCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRepairCreatedEvent(RepairCreatedEvent event) {
        log.info("Producing event, " + event);
        kafkaTemplate.send("repair-topic", event);
    }
}
