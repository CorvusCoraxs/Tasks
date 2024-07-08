package e.hw.taskfirstspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@SpringBootApplication
public class TaskFirstSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskFirstSpringBootApplication.class, args);
        System.out.println("Hello World!");
    }
    @Configuration
    static class SpringJdbcConfig {
        @Bean
        public DataSource mysqlDataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://212.113.123.118:3306/springjdbc");
            dataSource.setUsername("guest_user");
            dataSource.setPassword("guest_password");

            return dataSource;
        }
    }
}
