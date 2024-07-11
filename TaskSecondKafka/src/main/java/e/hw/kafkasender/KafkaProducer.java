package e.hw.kafkasender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    @Value("${kafka.topic}")
    private String topic;

    @Value("${file.path}")
    private String filePath;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessagesToKafka() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    kafkaTemplate.send(topic, line);
                    System.out.println("Sent message to Kafka: " + line);
                } catch (Exception e) {
                    logger.error("Error sending message to Kafka: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading file: {}", e.getMessage());
        }
    }
}