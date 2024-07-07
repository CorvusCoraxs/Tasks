package e.hw.tasksecondkafka;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    private KafkaProducer kafkaProducer;

    public HelloWorldController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping("/hello")
    public String helloWorld() {
        kafkaProducer.sendMessage("Hello, World!");
        return "Message to Kafka";
    }
}
