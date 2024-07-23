package net.plumbing.msgbus;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
//@Component
public class KafkaConsumer {

 //   private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
/*
    @Value("${kafka.topic}")
    private String topic;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @KafkaListener(
            topics = "${kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(ConsumerRecord<String, String> record, Consumer<?, ?> consumer) {
        logger.info("Получено сообщение из топика '{}': {}", topic, record.value());
    /*
        // Создаем Map для коммита
        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        TopicPartition partition = new TopicPartition(record.topic(), record.partition());
        offsets.put(partition, new OffsetAndMetadata(record.offset() + 1));

        // Коммитим офсет
        consumer.commitSync(offsets);

    }
    */
}