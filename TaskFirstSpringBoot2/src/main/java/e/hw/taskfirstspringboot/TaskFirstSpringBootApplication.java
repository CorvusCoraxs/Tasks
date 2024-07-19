package e.hw.taskfirstspringboot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class TaskFirstSpringBootApplication implements CommandLineRunner  {
    public static final Logger AppThead_log = LoggerFactory.getLogger(TaskFirstSpringBootApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TaskFirstSpringBootApplication.class, args);
    }

    public void run(String... args) throws Exception {
        int i;

        for (i = 0; i < args.length; i++) {}
    }

}

