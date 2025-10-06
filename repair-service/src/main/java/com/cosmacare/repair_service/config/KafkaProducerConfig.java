package com.cosmacare.repair_service.config;

import com.cosmacare.repair_service.entity.RepairCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    // 🔁 Common base config
    private Map<String, Object> baseConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

    // Kafka Config for Product
    @Bean
    public ProducerFactory<String, RepairCreatedEvent> repairProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfigs());
    }

    @Bean
    public KafkaTemplate<String, RepairCreatedEvent> repairKafkaTemplate() {
        return new KafkaTemplate<>(repairProducerFactory());
    }
}
