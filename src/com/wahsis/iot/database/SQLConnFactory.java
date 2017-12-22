/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.database;

import com.wahsis.iot.common.Config;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;
import snaq.db.ConnectionPool;
import java.util.*;
import java.util.logging.Level;

/**
 *
 * @author duclm2
 */
public class SQLConnFactory {

    private static final Logger logger = Logger.getLogger(SQLConnFactory.class);
    private static final String default_partner_server_id;
    private static final Hashtable partner_server;
    private static final Hashtable partner_server_company;

    static {
        partner_server = new Hashtable();
        partner_server_company = new Hashtable();
        List<PartnerServer> list = new ArrayList<>();
        List<PartnerServerCompany> list_company = new ArrayList<>();
        int uid = 0;
        try {
            uid = PartnerServerModelFactory.getDaoSQL().selectAllPartnerServer(list, list_company);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SQLConnFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (uid > 0) {
            PartnerServer defaults = list.get(0);
            default_partner_server_id = String.valueOf(defaults.getPartnerServerId());
            for (int i = 0; i < list.size(); i++) {
                PartnerServer obj = list.get(i);
                obj.initConnectionPool();
                partner_server.put(String.valueOf(obj.getPartnerServerId()), obj);
            }
            for (int i = 0; i < list_company.size(); i++) {
                PartnerServerCompany com = list_company.get(i);
                partner_server_company.put(String.valueOf(com.getCompanyId()), String.valueOf(com.getPartnerServerId()));
            }
        } else {
            default_partner_server_id = "1";
            PartnerServer obj = new PartnerServer(
                    Long.valueOf(default_partner_server_id),
                    Config.getParam("partner_sql_server", "host"),
                    Config.getParam("partner_sql_server", "port"),
                    Config.getParam("partner_sql_server", "dbname"),
                    Config.getParam("partner_sql_server", "username"),
                    Config.getParam("partner_sql_server", "password"),
                    1);
            obj.initConnectionPool();
            partner_server.put(obj.getPartnerServerId(), obj);
        }
    }

    public static Connection getConnection(String company_id) {
        String partner_server_id = default_partner_server_id;
        ConnectionPool partner_pool = null;
        if (partner_server_company.containsKey(company_id)) {
            partner_server_id = (String) partner_server_company.get(company_id);
        }
        if (partner_server.containsKey(Long.valueOf(partner_server_id))) {
            partner_pool = ((PartnerServer) partner_server.get(Long.valueOf(partner_server_id))).getConnectionPool();
        }
        if (partner_pool != null) {
            try {
                return partner_pool.getConnection(200);
            } catch (Exception ex) {
                logger.error("getConnection : " + ex.getMessage());
            }
        } else {
            return null;
        }
        return null;
    }

//    public static Connection getConnection() {
//        ConnectionPool partner_pool = ((PartnerServer) partner_server.get(default_partner_server_id)).getConnectionPool();
//        if (partner_pool != null) {
//            try {
//                return partner_pool.getConnection(200);
//            } catch (Exception ex) {
//                logger.error("getConnection : " + ex.getMessage());
//            }
//        } else {
//            return null;
//        }
//        return null;
//
//    }
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
