package net.plumbing.msgbus.threads;

import net.plumbing.msgbus.telegramm.NotifyByChannel;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.plumbing.msgbus.common.ApplicationProperties;
import net.plumbing.msgbus.threads.TheadDataAccess;

import static net.plumbing.msgbus.common.ApplicationProperties.DataSourcePoolMetadata;
// import static net.plumbing.msgbus.common.ApplicationProperties.bootstrapServers;

public class MessageReceiveTask implements Runnable {

    public static final Logger JMSReceiveTask_Log = LoggerFactory.getLogger(MessageReceiveTask.class);
    private TheadDataAccess theadDataAccess;

    public ConsumerFactory<String, String> makeConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationProperties.bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    public MessageReceiveTask() {
        super();
        this.theadDataAccess=null;
    }

    @Override
    public void run() {
        JMSReceiveTask_Log.warn("MessageReceiveTask , ThreadId=" + Thread.currentThread().threadId() + " is running, bootstrapServers=`" + ApplicationProperties.bootstrapServers + "`");

        this.theadDataAccess = new TheadDataAccess();

        JMSReceiveTask_Log.info("Установаливем 'соединение' , что бы зачитывать очередь: [" +
                ApplicationProperties.dbPoint + "] user:" + ApplicationProperties.dbLogin +
                "; passwd:" + ApplicationProperties.dbPasswd + ".");

        theadDataAccess.make_Hikari_Connection(
                ApplicationProperties.dbSchema,
                ApplicationProperties.dbLogin,
                ApplicationProperties.dataSource,
                JMSReceiveTask_Log
        );
        if (theadDataAccess.dbConnection == null) {
            JMSReceiveTask_Log.error("Ошибка на инициализации потока приёма Kafka-сообщений - theadDataAccess.make_Hikari_Connection('" + ApplicationProperties.dbSchema + "' , ...) return: NULL!");
            return;
        }

        for (;;) {

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(30));

                theadDataAccess.make_Hikari_Connection_Only(
                        ApplicationProperties.dbSchema,
                        ApplicationProperties.dbLogin,
                        ApplicationProperties.dataSource,
                        JMSReceiveTask_Log
                );
                if ( theadDataAccess.dbConnection == null ){
                    JMSReceiveTask_Log.error("Ошибка при получении соедининия с БД - theadDataAccess.make_Hikari_Connection_Only('"+ ApplicationProperties.dbSchema +"' , ...) return: NULL!"  );
                    return;
                }
                theadDataAccess.dbConnection.close();
                JMSReceiveTask_Log.info("DataSourcePool={}", DataSourcePoolMetadata.getActive());
            }
            catch (InterruptedException | SQLException e) {
                JMSReceiveTask_Log.error("do taskExecutor.shutdown! " + e.getMessage());
                if (theadDataAccess.dbConnection != null)
                    try {
                        theadDataAccess.dbConnection.close();
                    }
                    catch ( SQLException SQLe) {
                        JMSReceiveTask_Log.error(" С базой совсем всё плохо! " + SQLe.getMessage());
                        try {
                        NotifyByChannel.Telegram_sendMessage( "Stopping " + JMSReceiveTask_Log + " DBproblem `" +  SQLe.getMessage() +  "` :" + InetAddress.getLocalHost().getHostAddress()+ ", db " + ApplicationProperties.dataSource.getJdbcUrl()+ " ) , *stop!*", JMSReceiveTask_Log );
                        }
                        catch (java.net.UnknownHostException xe) {
                            JMSReceiveTask_Log.error( "NotifyByChannel.Telegram_sendMessage() fault: {}", xe.getMessage() );
                        }
                    }

                //  count = 0; // надо taskExecutor.shutdown();
                break;
            }
        }
    if (theadDataAccess.dbConnection != null) {
        try {
            theadDataAccess.dbConnection.close();
        } catch (SQLException e) {
            JMSReceiveTask_Log.error("theadDataAccess.dbConnection.close() fault {}", e.getMessage());
        }
    }

    }
}
