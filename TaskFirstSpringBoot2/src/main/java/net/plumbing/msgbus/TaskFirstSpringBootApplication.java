package net.plumbing.msgbus;
import net.plumbing.msgbus.config.TelegramProperties;
import net.plumbing.msgbus.telegramm.NotifyByChannel;
import net.plumbing.msgbus.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.net.InetAddress;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class TaskFirstSpringBootApplication implements CommandLineRunner  {
    public static final Logger AppThead_log = LoggerFactory.getLogger(TaskFirstSpringBootApplication.class);
    @Autowired
    public TelegramProperties telegramProperties;
    @Autowired
    public DatabaseConfig databaseConfig;

    public static final String ApplicationName="*Kafka_Consumer* v.0.07.18";
    public static String propJDBC;

    public static void main(String[] args) {
        SpringApplication.run(TaskFirstSpringBootApplication.class, args);
    }

    public void run(String... args) throws Exception {
        int i;
        AppThead_log.info("Hellow for Kafka_Consumer " + ApplicationName );
        NotifyByChannel.Telegram_setChatBotUrl( telegramProperties.getchatBotUrl() , AppThead_log );
        propJDBC = databaseConfig.getPoint();
        if ( propJDBC == null)  propJDBC = "jdbc UNKNOWN ! ";
        else {
            if ( propJDBC.indexOf("//") < 1  ) ; //propJDBC = "jdbc INVALID! `" + propJDBC + "`";
            else {
                propJDBC = propJDBC.substring(propJDBC.indexOf("//") + 2);
                if ( propJDBC.indexOf("/") < 1  ) propJDBC = "INVALID db in jdbc ! `" + propJDBC + "`";
                else
                    propJDBC = propJDBC.substring(0, propJDBC.indexOf("/"));
            }
        }
        NotifyByChannel.Telegram_sendMessage( "Starting "+ ApplicationName + " on " + InetAddress.getLocalHost().getHostName()+ " (ip `" +InetAddress.getLocalHost().getHostAddress() + "`, db `" + propJDBC+ "` ) ", AppThead_log );

        for (i = 0; i < args.length; i++) {
            AppThead_log.info("arg [" + i + "]" + args[i] );
        }
    }

}

