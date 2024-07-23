package net.plumbing.msgbus;

import net.plumbing.msgbus.common.HikariDataAccess;
import net.plumbing.msgbus.config.Receiver_AppConfig;
import net.plumbing.msgbus.threads.MessageReceiveTask;
import net.plumbing.msgbus.threads.TheadDataAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


import net.plumbing.msgbus.common.ApplicationProperties;
import net.plumbing.msgbus.config.TelegramProperties;
import net.plumbing.msgbus.telegramm.NotifyByChannel;
import net.plumbing.msgbus.config.DatabaseConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static net.plumbing.msgbus.common.ApplicationProperties.DataSourcePoolMetadata;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class KafkaConsumerApplication implements CommandLineRunner  {
    public static final Logger AppThead_log = LoggerFactory.getLogger(KafkaConsumerApplication.class);
    @Autowired
    public TelegramProperties telegramProperties;
    @Autowired
    public DatabaseConfig databaseConfig;

    public static final String ApplicationName="*Kafka_Consumer* v.0.07.18";
    public static String propJDBC;

    public static void main(String[] args) {
        SpringApplication.run(KafkaConsumerApplication.class, args);
    }

    public void run(String... args) throws Exception {
        int i;

        ApplicationContext context = new AnnotationConfigApplicationContext(Receiver_AppConfig.class);
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
        // сохраняем ApplicationProperties в статическом классе для простоты доступа из потоков

        ApplicationProperties.WaitTimeBetweenScan = Integer.parseInt( databaseConfig.getwaitTimeScan() );
        ApplicationProperties.dbSchema =  databaseConfig.getSchema() ;
        AppThead_log.info("dbSchema = " + ApplicationProperties.dbSchema );
        ApplicationProperties.dbPasswd =  databaseConfig.getPassword() ;
        ApplicationProperties.dbLogin = databaseConfig.getLogin();
        ApplicationProperties.dbPasswd =  databaseConfig.getPassword();
        ApplicationProperties.dbPoint = databaseConfig.getPoint();

        try {
            ApplicationProperties.dataSource = HikariDataAccess.HiDataSource (ApplicationProperties.dbPoint,
                    ApplicationProperties.dbLogin,
                    ApplicationProperties.dbPasswd
            );
            ApplicationProperties.DataSourcePoolMetadata = HikariDataAccess.DataSourcePoolMetadata;

        } catch (Exception e) {
            AppThead_log.error("НЕ удалось подключится к базе данных (`" + ApplicationProperties.dbPoint + "` ) транспортных сообщений:" + e.getMessage());
            System.exit(-19);
        }
        AppThead_log.info("message DataSource = " + ApplicationProperties.dataSource );
        if ( ApplicationProperties.dataSource != null )
        {
            AppThead_log.info("message DataSource = " + ApplicationProperties.dataSource
                    + " JdbcUrl:" + ApplicationProperties.dataSource.getJdbcUrl()
                    + " isRunning:" + ApplicationProperties.dataSource.isRunning()
                    + " 4 dbSchema:" + ApplicationProperties.dbSchema);
        }
        else {
            AppThead_log.error("message DataSource is NULL");
            System.exit(-21);
        }
        TheadDataAccess theadDataAccess = new TheadDataAccess();
        theadDataAccess.make_Hikari_Connection_Only(
                                    ApplicationProperties.dbSchema,
                                    ApplicationProperties.dbLogin,
                                    ApplicationProperties.dataSource,
                                    AppThead_log
        );
        if ( theadDataAccess.dbConnection == null ){
            AppThead_log.error("Ошибка при получении соедининия с БД - theadDataAccess.make_Hikari_Connection('"+ ApplicationProperties.dbSchema +"' , ...) return: NULL!"  );
            return;
        }
        theadDataAccess.dbConnection.close();

        ApplicationProperties.bootstrapServers = databaseConfig.getbootstrapServers();
        int TotalNumTasks = Integer.parseInt(databaseConfig.gettotalNumTasks());
        AppThead_log.info("bootstrapServers = {}; TotalNumTasks {}" , databaseConfig.getbootstrapServers(), databaseConfig.gettotalNumTasks() );

        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
        MessageReceiveTask[] kafkaReceiveTask = new MessageReceiveTask[TotalNumTasks];
        Thread.State ReceiveThreadState = Thread.State.NEW;
        Thread[] JMSReceiveThread = new Thread[TotalNumTasks];

        int CurrentTasksIndex ;
        for (CurrentTasksIndex = 0; CurrentTasksIndex < TotalNumTasks; CurrentTasksIndex++) {
            // не нужен MessageDirectionsCode =  MessageRepositoryHelper.look4MessageDirectionsCode_4_Num_Thread( theadNum + this.FirstInfoStreamId  , AppThead_log );
            kafkaReceiveTask[CurrentTasksIndex] = new MessageReceiveTask();// (MessageSendTask) context.getBean("MessageSendTask");
            JMSReceiveThread[CurrentTasksIndex] = taskExecutor.createThread( kafkaReceiveTask[CurrentTasksIndex] );

            taskExecutor.execute(JMSReceiveThread[CurrentTasksIndex]);
            AppThead_log.info("ReceiveThread[" + CurrentTasksIndex + "] on bootstrapServers=" + ApplicationProperties.bootstrapServers + " run: " + JMSReceiveThread[CurrentTasksIndex].getName() + "; JMSReceiveThread_Id=" + JMSReceiveThread[CurrentTasksIndex].getId() + "; isAlive=" + JMSReceiveThread[CurrentTasksIndex].isAlive());
            ReceiveThreadState = JMSReceiveThread[CurrentTasksIndex].getState();
            AppThead_log.info("JMSReceiveThread[" + CurrentTasksIndex + "] JMSReceiveThreadState: " + ReceiveThreadState.toString() ) ;
            // taskExecutor.execute(kafkaReceiveTask[ i ]);
        }
        for (;;) {
            //AppThead_log.info("Active Threads : " + count);
            AppThead_log.info("DataSourcePool getMax: " + ApplicationProperties.DataSourcePoolMetadata.getMax()
                    + ", getIdle: " + ApplicationProperties.DataSourcePoolMetadata.getIdle()
                    + ", getActive: " + ApplicationProperties.DataSourcePoolMetadata.getActive()
                    + ", getMax: " + ApplicationProperties.DataSourcePoolMetadata.getMax()
                    + ", getMin: " + ApplicationProperties.DataSourcePoolMetadata.getMin()
            );
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(30));
                Runtime runTime = Runtime.getRuntime();
                long freeMemory = runTime.maxMemory() - runTime.totalMemory() + runTime.freeMemory();
                AppThead_log.info(" 'free memory' of a Java process before GC is : " + freeMemory);
                runTime.gc();
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                freeMemory = runTime.maxMemory() - runTime.totalMemory() + runTime.freeMemory();
                AppThead_log.info(" 'free memory' of a Java process after GC is : " + freeMemory);

                theadDataAccess.make_Hikari_Connection_Only(
                        ApplicationProperties.dbSchema,
                        ApplicationProperties.dbLogin,
                        ApplicationProperties.dataSource,
                        AppThead_log
                );
                if ( theadDataAccess.dbConnection == null ){
                    AppThead_log.error("Ошибка при получении соедининия с БД - theadDataAccess.make_Hikari_Connection('"+ ApplicationProperties.dbSchema +"' , ...) return: NULL!"  );
                    return;
                }
                theadDataAccess.dbConnection.close();
                AppThead_log.info("Response.Status=" +  //getResponse.getStatus() +
                        "; DataSourcePool=" + DataSourcePoolMetadata.getActive());
            }
            catch (InterruptedException | SQLException e) {
                AppThead_log.error("do taskExecutor.shutdown! " + e.getMessage());
                e.printStackTrace();
                NotifyByChannel.Telegram_sendMessage( "Do "+ ApplicationName + " taskExecutor.Shutdown -`" +  e.getMessage() +  "` :" + InetAddress.getLocalHost().getHostAddress()+ ", db " + propJDBC+ " ) , *exit!*", AppThead_log );
                if (theadDataAccess.dbConnection != null)
                    try {
                        theadDataAccess.dbConnection.close();
                    }
                    catch ( SQLException SQLe) {
                        AppThead_log.error(" С базой совсем всё плохо! " + SQLe.getMessage());
                        NotifyByChannel.Telegram_sendMessage( "Stopping " + ApplicationName + " DBproblem `" +  SQLe.getMessage() +  "` :" + InetAddress.getLocalHost().getHostAddress()+ ", db " + propJDBC+ " ) , *exit!*", AppThead_log );
                    }

              //  count = 0; // надо taskExecutor.shutdown();
                break;
            }
        }
    }

}

