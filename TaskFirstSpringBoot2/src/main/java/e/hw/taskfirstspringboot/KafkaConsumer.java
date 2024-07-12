package e.hw.taskfirstspringboot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    private KafkaConsumerSender kafkaConsumerSender;

    @KafkaListener(topics = "${kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(String message) {
        logger.info("Получено сообщение из топика '{}': {}", topic, message);
        kafkaConsumerSender.sendMessageToDb(topic, message);
    }
}