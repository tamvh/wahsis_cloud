/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.model;

import com.wahsis.iot.common.CommonService;
import com.wahsis.iot.database.SQLConnFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import java.util.Date;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author tamvh
 */
public class AreaModel {
    private static AreaModel _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    protected final Logger logger = Logger.getLogger(this.getClass());
   
    public static AreaModel getInstance() throws IOException {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new AreaModel();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    
    public long updateOnOffLightArea(String company_id, long area_id, int area_on_off) {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement cs = null;
        long uid = -1;
        try {
            connection = SQLConnFactory.getConnection(company_id);
            cs = connection.prepareStatement("SET NOCOUNT ON ; "
                    + "update " + CommonService.getTableName(company_id, "room_area") + " "
                    + "set "
                    + "[on_off] = ? "
                    + "where area_id = ? ; "
                    + "SELECT 1 as result;");
            int count = 1;
            cs.setInt(count++, area_on_off);
            cs.setLong(count++, area_id);
            rs = cs.executeQuery();
            if (rs.next()) {
                uid = rs.getLong(1);
            }
        } catch (SQLException ex) {
            logger.error("AreaModel.updateOnOffLightArea: " + ex.getMessage(), ex);
        } finally {
            SQLConnFactory.safeClose(rs);
            SQLConnFactory.safeClose(cs);
            SQLConnFactory.safeClose(connection);
        }
        return uid;
    }
}
