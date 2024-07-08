package e.hw.taskfirstspringboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootApplication
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
        logger.info("paradit.extsys-db-schema: {}", config.getSchema());
        logger.info("paradit.extsys-db-login: {}", config.getLogin());
        logger.info("paradit.extsys-db-password: {}", config.getPassword());
        logger.info("paradit.extsys-db-data-source-class-name: {}", config.getDataSourceClassName());
        System.out.println("HW");
    }

    @Configuration
    static class SpringJdbcConfig {
        @Bean
        public DataSource mysqlDataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl("jdbc:postgresql://127.0.0.1:5432/pgdb?ApplicationName=Kaffka_DevConsumer");
            return dataSource;
        }
    }
}
















