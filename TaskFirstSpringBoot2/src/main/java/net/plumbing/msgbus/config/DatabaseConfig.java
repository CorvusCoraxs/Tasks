package net.plumbing.msgbus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;

//import javax.sql.DataSource;

@Configuration
@ConfigurationProperties("paradit.extsys")
public  class DatabaseConfig {
    private static String point;
    private String schema;
    private String login;
    private String password;
    private String dataSourceClassName;

/*
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(getDataSourceClassName());
        dataSource.setUrl(getPoint());
        dataSource.setUsername(getLogin());
        dataSource.setPassword(getPassword());
        return dataSource;
    }
*/
    public  String getPoint() {
        return this.point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDataSourceClassName() {
        return dataSourceClassName;
    }

    public void setDataSourceClassName(String dataSourceClassName) {
        this.dataSourceClassName = dataSourceClassName;
    }
    private String waitTimeScan;
    public String getwaitTimeScan() {
        return this.waitTimeScan;
    }
    public void setwaitTimeScan(String waitTimeScan) {
        this.waitTimeScan = waitTimeScan;
    }

    private String bootstrapServers;
    public String getbootstrapServers() { return this.bootstrapServers; }
    public void setbootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    private String totalNumTasks;
    public String gettotalNumTasks() {
        return totalNumTasks;
    }
    public void settotalNumTasks(String totalNumTasks) {
        this.totalNumTasks = totalNumTasks;
    }
}
