package net.plumbing.msgbus.common;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.plumbing.msgbus.KafkaConsumerApplication;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

public class HikariDataAccess {
    public static HikariDataSourcePoolMetadata DataSourcePoolMetadata = null;
    @Bean (destroyMethod = "close")
    public static  HikariDataSource HiDataSource(String JdbcUrl, String Username, String Password ){
        HikariConfig hikariConfig = new HikariConfig();
        String connectionUrl ;
        if ( JdbcUrl==null) {
            connectionUrl = "jdbc:oracle:thin:@//127.0.0.1:1521/orm"; //  !!!
        }
        else {
            connectionUrl = JdbcUrl;
        }
        String ClassforName;
        if ( connectionUrl.indexOf("oracle") > 0 )
            ClassforName = "oracle.jdbc.driver.OracleDriver";
        else ClassforName = "org.postgresql.Driver";

//        hikariConfig.setDriverClassName("oracle.jdbc.driver.OracleDriver");
//        hikariConfig.setJdbcUrl( "jdbc:oracle:thin:@"+ JdbcUrl); //("jdbc:oracle:thin:@//10.242.36.8:1521/hermes12");
        KafkaConsumerApplication.AppThead_log.info( "Try make hikariConfig: " + connectionUrl + " as " + Username + " , Class.forName:" + ClassforName);
        hikariConfig.setDriverClassName(ClassforName);
        hikariConfig.setJdbcUrl(  connectionUrl ); //("jdbc:oracle:thin:@//10.242.36.8:1521/hermes12");

        hikariConfig.setUsername( Username ); //("ARTX_PROJ");
        hikariConfig.setPassword( Password ); // ("rIYmcN38St5P");

        hikariConfig.setLeakDetectionThreshold(TimeUnit.MINUTES.toMillis(5));
        hikariConfig.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30));
        hikariConfig.setValidationTimeout(TimeUnit.MINUTES.toMillis(1));
        hikariConfig.setIdleTimeout(TimeUnit.MINUTES.toMillis(5));
        hikariConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(10));

        hikariConfig.setMaximumPoolSize(100);
        hikariConfig.setMinimumIdle(10);
        if ( connectionUrl.indexOf("oracle") > 0 )
            hikariConfig.setConnectionTestQuery("SELECT 1 from dual");
        else
            hikariConfig.setConnectionTestQuery("SELECT 1 ");
        hikariConfig.setPoolName("MessageCP");

        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", "100");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "4096");
        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("dataSource.autoCommit", "false");
        KafkaConsumerApplication.AppThead_log.info( "Try make DataSourcePool: " + connectionUrl + " as " + Username + " , Class.forName:" + ClassforName);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        //HikariPool hikariPool = new HikariPool(hikariConfig);
        DataSourcePoolMetadata = new HikariDataSourcePoolMetadata(dataSource);
        KafkaConsumerApplication.AppThead_log.info( "DataSourcePool ( at start ): getMax: " + DataSourcePoolMetadata.getMax()
                + ", getIdle: " + DataSourcePoolMetadata.getIdle()
                + ", getActive: " + DataSourcePoolMetadata.getActive()
                + ", getMax: " + DataSourcePoolMetadata.getMax()
                + ", getMin: " + DataSourcePoolMetadata.getMin()
        );
        KafkaConsumerApplication.AppThead_log.info(
                "ConnectionTestQuery: " + dataSource.getConnectionTestQuery()
                        + ", IdleTimeout: " + dataSource.getIdleTimeout()
                        + ", LeakDetectionThreshold: " + dataSource.getLeakDetectionThreshold()
        );

        try {

            Connection tryConn = dataSource.getConnection();
            PreparedStatement prepareStatement;
            if ( connectionUrl.indexOf("oracle") > 0 )
                prepareStatement = tryConn.prepareStatement( "SELECT 1 from dual");
            else
                prepareStatement = tryConn.prepareStatement( "SELECT 1 ");
            prepareStatement.executeQuery();
            prepareStatement.close();
            KafkaConsumerApplication.AppThead_log.info( "DataSourcePool ( at prepareStatement ): getMax: " + DataSourcePoolMetadata.getMax()
                    + ", getIdle: " + DataSourcePoolMetadata.getIdle()
                    + ", getActive: " + DataSourcePoolMetadata.getActive()
                    + ", getMax: " + DataSourcePoolMetadata.getMax()
                    + ", getMin: " + DataSourcePoolMetadata.getMin()
            );
            tryConn.close();
            KafkaConsumerApplication.AppThead_log.info( "getJdbcUrl: "+ hikariConfig.getJdbcUrl());
        }
        catch (java.sql.SQLException e)
        { KafkaConsumerApplication.AppThead_log.error( e.getMessage());}


        return dataSource;
    }
    /* */
}
