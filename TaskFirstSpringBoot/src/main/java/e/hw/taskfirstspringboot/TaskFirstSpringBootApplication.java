package e.hw.taskfirstspringboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class TaskFirstSpringBootApplication implements CommandLineRunner {


    @Autowired
    private MyConfig config;

    private static final Logger logger = LoggerFactory.getLogger(TaskFirstSpringBootApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TaskFirstSpringBootApplication.class, args);

    }
    @Override
    public void run(String... args) throws Exception {
        logger.info("paradit.extsys-db-point: {}", config.getPoint());
        logger.info("paradit.extsys-db-login: {}", config.getLogin());
        logger.info("paradit.extsys-db-password: {}", config.getPassword());
        logger.info("paradit.extsys-db-data-source-class-name: {}", config.getDataSourceClassName());
        logger.info("Hello World");
    }
}
















