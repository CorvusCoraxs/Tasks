package net.plumbing.msgbus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class KafkaConsumerSender {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    public void sendMessageToDb(String topic, String message) {
        jdbcTemplate.update("INSERT INTO messages (topic, message) VALUES (?, ?)", topic, message);
    }
}


