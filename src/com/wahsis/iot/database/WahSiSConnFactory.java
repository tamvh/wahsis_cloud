/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.database;

import com.wahsis.iot.common.Config;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
import snaq.db.ConnectionPool;

/**
 *
 * @author khiemnv
 */
public class WahSiSConnFactory {

    private static final Logger logger = Logger.getLogger(WahSiSConnFactory.class);
    private static final String SQL_SERVER_HOST = Config.getParam("wahsis_sql_server", "host");
    private static final String SQL_SERVER_PORT = Config.getParam("wahsis_sql_server", "port");
    private static final String SQL_SERVER_DBNAME = Config.getParam("wahsis_sql_server", "dbname");
    private static final String SQL_SERVER_USER = Config.getParam("wahsis_sql_server", "username");
    private static final String SQL_SERVER_PASS = Config.getParam("wahsis_sql_server", "password");

    private static final ConnectionPool pos_live_pool;
    private static final String strConnectString;

    static {
        strConnectString = "jdbc:sqlserver://" + SQL_SERVER_HOST + ":" + SQL_SERVER_PORT + ";databaseName=" + SQL_SERVER_DBNAME + ";user=" + SQL_SERVER_USER + ";password=" + SQL_SERVER_PASS;
        pos_live_pool = new ConnectionPool("pos_live_pool", 5, 20, 60, 180, strConnectString, SQL_SERVER_USER, SQL_SERVER_PASS);
    }

    public static Connection getConnection() {

        if (pos_live_pool != null) {
            try {
                return pos_live_pool.getConnection(200);
            } catch (Exception ex) {
                logger.error("getConnection : " + ex.getMessage());
            }
        } else {
            return null;
        }
        return null;

    }

    public static void safeClose(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("safeClose.Connection:" + e.getMessage(), e);
            }
        }
    }

    public static void safeClose(ResultSet res) {
        if (res != null) {
            try {
                res.close();
            } catch (SQLException e) {
                logger.error("safeClose.ResultSet:" + e.getMessage(), e);
            }
        }
    }

    public static void safeClose(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                logger.error("safeClose.Statement:" + e.getMessage(), e);
            }
        }
    }

    public static void safeClose(PreparedStatement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                logger.error("safeClose.Statement:" + e.getMessage(), e);
            }
        }
    }
}
