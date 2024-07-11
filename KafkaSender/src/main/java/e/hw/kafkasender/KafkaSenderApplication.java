package e.hw.kafkasender;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class KafkaSenderApplication implements CommandLineRunner {

    private final KafkaProducer kafkaProducer;

    public KafkaSenderApplication(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaSenderApplication.class, args);
    }

    @Override
    public void run(String... args) {
        kafkaProducer.sendMessagesToKafka();
    }
}