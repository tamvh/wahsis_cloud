package com.wahsis.iot.database;

import java.util.Date;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

import com.wahsis.iot.database.WahSiSConnFactory;

/*
* @author khiemnv
 */
public class PartnerServerModelSQL implements PartnerServerModel {

    private static PartnerServerModelSQL _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    protected final Logger logger = Logger.getLogger(this.getClass());

    public static PartnerServerModelSQL getInstance() throws IOException {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new PartnerServerModelSQL();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }

    public int selectAllPartnerServer(List<PartnerServer> listPartnerServer, List<PartnerServerCompany> listPartnerServerCompany) {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement cs = null;
        int uid = -1;
        //List<PartnerServer> listPartnerServer = new ArrayList<>();
        try {

            connection = WahSiSConnFactory.getConnection();
            if (connection != null) {
                cs = connection.prepareStatement("SET NOCOUNT ON; "
                        + "select * from  partner_server order by is_default desc; \n "
                        + "select * from  partner_server_company order by partner_server_id, company_id asc ; \n "
                        + "");
                boolean kq = cs.execute();
                if (kq) {
                    rs = cs.getResultSet();//.executeQuery();
                    while (rs.next()) {
                        PartnerServer partnerserver = new PartnerServer();
                        partnerserver.setPartnerServerId(rs.getLong("partner_server_id"));
                        partnerserver.setServerName(rs.getString("server_name"));
                        partnerserver.setPortNumber(rs.getString("port_number"));
                        partnerserver.setDatabaseName(rs.getString("database_name"));
                        partnerserver.setUserName(rs.getString("user_name"));
                        partnerserver.setPassword(rs.getString("password"));
                        partnerserver.setIsDefault(rs.getInt("is_default"));
                        /*PartnerServer partnerserver = new PartnerServer(
                                rs.getString("server_name"),
                                rs.getString("port_number"),
                                rs.getString("database_name"),
                                rs.getString("user_name"),
                                rs.getString("password"));*/
                        // partnerserver.setIsDefault(rs.getInt("is_default"));
                        // partnerserver.setPartnerServerId(rs.getLong("partner_server_id"));
                        listPartnerServer.add(partnerserver);
                    }
                    kq = cs.getMoreResults();
                    if (kq) {
                        rs = cs.getResultSet();
                        while (rs.next()) {
                            PartnerServerCompany obj = new PartnerServerCompany();
                            obj.setCompanyId(rs.getInt("company_id"));
                            obj.setPartnerServerId(rs.getLong("partner_server_id"));
                            listPartnerServerCompany.add(obj);
                        }
                    }
                    uid = 1;
                }

            }
        } catch (Exception ex) {
            logger.error("PartnerServerModelSQL.selectAllPartnerServer : " + ex.getMessage(), ex);
        } finally {
            SQLConnFactory.safeClose(rs);
            SQLConnFactory.safeClose(cs);
            SQLConnFactory.safeClose(connection);
        }
        return uid;// listPartnerServer;
    }
}
