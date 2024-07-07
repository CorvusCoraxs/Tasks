package e.hw.tasksecondkafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class MessageListener {
    @KafkaListener(topics = "mytopic", groupId = "my-group")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}

