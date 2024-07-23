package net.plumbing.msgbus.threads;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import java.sql.*;

public class TheadDataAccess {

    public  Connection dbConnection =null;
    private  String dbSchema="orm";
    private String rdbmsVendor;
    private  String UPDATE_QUEUElog_Response;
    private PreparedStatement stmt_UPDATE_QUEUElog;
    private  String INSERT_QUEUElog_Request;
    // TODO Для Oracle используется call insert into returning ROWID into
    public CallableStatement stmt_INSERT_QUEUElog;

    public Connection make_Hikari_Connection_Only( String db_userid , String db_password,
                                                   HikariDataSource dataSource,
                                                   Logger dataAccess_log) {
        Connection Target_Connection ;
        String connectionUrl= dataSource.getJdbcUrl() ;
        // попробуй ARTX_PROJ / rIYmcN38St5P  || hermes / uthvtc
        //String db_userid = "HERMES";
        //String db_password = "uthvtc";
        if (connectionUrl.indexOf("oracle") > 0) {
            rdbmsVendor = "oracle";
        } else {
            rdbmsVendor = "postgresql";
        }
        dataAccess_log.info( "Try(thead) MessegeDB getConnection: " + connectionUrl + " as " + db_userid );

        try {
            dbConnection = dataSource.getConnection();
            dbConnection.setAutoCommit(false);

            if (!rdbmsVendor.equals("oracle")) {
                PreparedStatement stmt_SetTimeZone = dbConnection.prepareStatement("set SESSION time zone 3");//.nativeSQL( "set SESSION time zone 3" );
                stmt_SetTimeZone.execute();
                stmt_SetTimeZone.close();
            }

        } catch (SQLException e) {
            dataAccess_log.error( e.getMessage() );
            e.printStackTrace();
            return (  null );
        }
        dataAccess_log.info( "Hermes(thead) getConnection: " + connectionUrl + " as " + db_userid + " done" );
        Target_Connection = dbConnection;
        return Target_Connection;
    }

    public Connection make_Hikari_Connection(  String HrmsSchema,
                                               String db_userid ,
                                               HikariDataSource dataSource,
                                               Logger dataAccess_log) {
        Connection Target_Connection ;
        String connectionUrl= dataSource.getJdbcUrl() ;
        // попробуй ARTX_PROJ / rIYmcN38St5P  || hermes / uthvtc
        //String db_userid = "HERMES";
        //String db_password = "uthvtc";
        this.dbSchema = HrmsSchema;
        if (connectionUrl.indexOf("oracle") > 0) {
            rdbmsVendor = "oracle";
        } else {
            rdbmsVendor = "postgresql";
        }
        dataAccess_log.info( "Try(thead) Hermes getConnection: " + connectionUrl + " as " + db_userid + " rdbmsVendor=" + rdbmsVendor);

        if ( dbConnection == null)
            try {
                dbConnection = dataSource.getConnection();
                dbConnection.setAutoCommit(false);
            } catch (SQLException e) {
                dataAccess_log.error( "make_Hikari_Connection: `" + connectionUrl + "` fault:" + e.getMessage() );
                if ( dbConnection != null)
                    try {
                        dbConnection.close();
                    } catch ( SQLException SQLe) {
                        dataAccess_log.error( "make_Hikari_Connection close() for : `" + connectionUrl + "` fault:" + e.getMessage() );
                    }
                e.printStackTrace();
                return (  null );
            }
        // dataAccess_log.info( "Hermes(thead) getConnection: " + connectionUrl + " as " + db_userid + " done" );
        Target_Connection = dbConnection;

        if (!rdbmsVendor.equals("oracle")) {
            try {
                PreparedStatement stmt_SetTimeZone = dbConnection.prepareStatement("set SESSION time zone 3");//.nativeSQL( "set SESSION time zone 3" );
                stmt_SetTimeZone.execute();
                stmt_SetTimeZone.close();
            }catch (SQLException e) {
                dataAccess_log.error("make_Hikari_Connection stmt_SetTimeZone PreparedStatement for: `" + connectionUrl + "` fault:" + e.getMessage() );
                e.printStackTrace();
                if ( dbConnection != null)
                    try {
                        dbConnection.close();
                    } catch ( SQLException SQLe) {
                        dataAccess_log.error( "make_Hikari_Connection close() for : `" + connectionUrl + "` fault:" + e.getMessage() );
                    }
                return ( null);
            }
        }



        if ( make_UPDATE_QUEUElog(dataAccess_log) == null ) {
            dataAccess_log.error( "make_UPDATE_QUEUElog() fault");
            return null;
        }

        if ( make_INSERT_QUEUElog(dataAccess_log) == null ) {
            dataAccess_log.error( "make_UPDATE_QUEUElog() fault");
            return null;
        }

        return Target_Connection;
    }

    public PreparedStatement make_UPDATE_QUEUElog(Logger dataAccess_log) {
        PreparedStatement StmtMsg_Queue;
        UPDATE_QUEUElog_Response = "update " + dbSchema + ".MESSAGE_QUEUElog set Resp_DT = current_timestamp, Response = ? where QUEUE_ID= ? and ROWID = ?";
        try {

            StmtMsg_Queue = (PreparedStatement) this.dbConnection.prepareStatement(UPDATE_QUEUElog_Response);
        } catch (Exception e) {
            dataAccess_log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }

        this.stmt_UPDATE_QUEUElog = StmtMsg_Queue;
        return StmtMsg_Queue;
    }

    private PreparedStatement // TODO Ora: CallableStatement
    make_INSERT_QUEUElog(Logger dataAccess_log) {
        if (!rdbmsVendor.equals("oracle")) {
            // TODO 4_Postgre
            PreparedStatement StmtMsg_Queue;
            INSERT_QUEUElog_Request="insert into " + dbSchema + ".MESSAGE_QUEUElog  ( Queue_Id, Req_dt, RowId, Request ) values( ?, current_timestamp, cast(nextval( '"+ dbSchema + ".message_queuelog_seq') as varchar), ?) ";
            try {  StmtMsg_Queue = this.dbConnection.prepareCall(INSERT_QUEUElog_Request);
            } catch (Exception e) {
                dataAccess_log.error(e.getMessage());
                e.printStackTrace();
                return ((PreparedStatement) null);
            }
            this.stmt_INSERT_QUEUElog = (CallableStatement)StmtMsg_Queue;
            // TODO RowId Postgree RowId
            // this.stmt_INSERT_QUEUElog = StmtMsg_Queue;
            return StmtMsg_Queue;
        }
        else
        { CallableStatement StmtMsg_Queue;
            INSERT_QUEUElog_Request = "{call insert into " + dbSchema + ".MESSAGE_QUEUElog L ( Queue_Id, Req_dt, request ) values( ?, systimestamp, ?) returning ROWID into ? }";
            try {  StmtMsg_Queue = this.dbConnection.prepareCall(INSERT_QUEUElog_Request);
            } catch (Exception e) {
                dataAccess_log.error(e.getMessage());
                e.printStackTrace();
                return ((CallableStatement) null);
            }
            this.stmt_INSERT_QUEUElog = (CallableStatement)StmtMsg_Queue;
            // TODO RowId Oracle native RowId
            return StmtMsg_Queue;
        }
    }
}
