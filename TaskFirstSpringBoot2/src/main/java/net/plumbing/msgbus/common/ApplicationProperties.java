package net.plumbing.msgbus.common;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;

public  class ApplicationProperties {
    public static String dbSchema;
    public static String dbPoint;
    public static String dbLogin;
    public static String dbPasswd;

    public static Long TotalTimeTasks;
    public static Integer WaitTimeBetweenScan;
    public static Integer ApiRestWaitTime;
    public static Integer ShortRetryCount;
    public static Integer LongRetryCount;
    public static Integer ShortRetryInterval;
    public static Integer LongRetryInterval ;
    public static String ConnectMsgBus;
    public static String pSQLFunctionRun;



    public static HikariDataSource dataSource; //= HiDataSource();
    public static String bootstrapServers;
    public static HikariDataSourcePoolMetadata DataSourcePoolMetadata;
    public static HikariDataSourcePoolMetadata extSystemDataSourcePoolMetadata;
}
