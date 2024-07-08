package e.hw.taskfirstspringboot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Configuration
@ConfigurationProperties(prefix = "paradit.extsys")
    public class MyConfig {
    private String point;
    private String schema;
    private String login;
    private String password;
    private String dataSourceClassName;

    public String getPoint() {
        return point;
    }

    public String getSchema() {
        return schema;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }
}