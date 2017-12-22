/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.model;

import com.wahsis.iot.common.CommonService;
import com.wahsis.iot.data.Light;
import com.wahsis.iot.database.SQLConnFactory;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

/**
 *
 * @author diepth
 */
public class LightModel {
    
    private static LightModel _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    protected final Logger logger = Logger.getLogger(this.getClass());
    private static Map<String, Light> _mapLightInfo = Collections.synchronizedMap(new LinkedHashMap<String, Light>());
   
    public static LightModel getInstance() throws IOException {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new LightModel();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    
    public int updateOnOffByGroupId(String company_id, String list_light_code, int onoff) {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement cs = null;
        int ret = -1;
        try {
            int brightness = 0;
            if(onoff == 1) {
                brightness = 100;
            }
            
            connection = SQLConnFactory.getConnection(company_id);
            cs = connection.prepareStatement("SET NOCOUNT ON ; \n "
                    + " update " + CommonService.getTableName(company_id, "room_area_light") + " \n "
                    + " set [on_off] = ? , [brightness] = ? \n "
                    + " where light_code IN('1,'2','3') ; \n  "
                    + " SELECT 1 as result; \n "
                    + " select top 1 l.* \n "
                    + " from " + CommonService.getTableName(company_id, "room_area_light") + " as l \n "
                    + " where l.light_code IN('1,'2','3') "
                    + "");
            int count = 1;
            cs.setInt(count++, onoff);
            cs.setInt(count++, brightness);
            cs.setString(count++, list_light_code);
            cs.setString(count++, list_light_code);
            boolean kq = cs.execute();
            if (kq) {
                rs = cs.getResultSet();
                if (rs.next()) {
                    if (rs.getLong(1) > 0 && cs.getMoreResults()) {
                        rs = cs.getResultSet();
                        if (rs.next()) {
                            ret = 0;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("RoomAreaLightModelSQL.updateOnOffByID : " + ex.getMessage(), ex);
        } finally {
            SQLConnFactory.safeClose(rs);
            SQLConnFactory.safeClose(cs);
            SQLConnFactory.safeClose(connection);
        }
        return ret;
    }
    
    public int updateOnOffByID(String company_id, Light light) {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement cs = null;
        int ret = -1;
        try {
            int brightness = 0;
            // light type: 0(dim), 1(onoff)
            
            connection = SQLConnFactory.getConnection(company_id);
            if(connection == null) {
                return ret;
            }
            cs = connection.prepareStatement("SET NOCOUNT ON ; \n "
                    + " update " + CommonService.getTableName(company_id, "room_area_light") + " \n "
                    + " set [on_off] = ? , [brightness] = ? \n "
                    + " where light_code = ? ; \n  "
                    + " SELECT 1 as result; \n "
                    + " select top 1 l.* \n "
                    + " from " + CommonService.getTableName(company_id, "room_area_light") + " as l \n "
                    + " where l.light_code = ? "
                    + "");
            int count = 1;
            cs.setInt(count++, light.getOn_off());
            cs.setInt(count++, brightness);
            cs.setString(count++, light.getLight_code());
            cs.setString(count++, light.getLight_code());
            boolean kq = cs.execute();
            if (kq) {
                rs = cs.getResultSet();
                if (rs.next()) {
                    if (rs.getLong(1) > 0 && cs.getMoreResults()) {
                        rs = cs.getResultSet();
                        if (rs.next()) {
                            ret = 0;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("RoomAreaLightModelSQL.updateOnOffByID : " + ex.getMessage(), ex);
        } finally {
            SQLConnFactory.safeClose(rs);
            SQLConnFactory.safeClose(cs);
            SQLConnFactory.safeClose(connection);
        }
        return ret;
    }
    
    public int updateBrightnessByID(String company_id, Light light) {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement cs = null;
        int ret = -1;
        try {
            int brightness = light.getBrightness();
            connection = SQLConnFactory.getConnection(company_id);
            cs = connection.prepareStatement("SET NOCOUNT ON ; \n "
                    + " update " + CommonService.getTableName(company_id, "room_area_light") + " \n "
                    + " set [on_off] = ? , [brightness] = ? \n "
                    + " where light_code = ? ; \n  "
                    + " SELECT 1 as result; \n "
                    + " select top 1 l.* \n "
                    + " from " + CommonService.getTableName(company_id, "room_area_light") + " as l \n "
                    + " where l.light_code = ? "
                    + "");
            int count = 1;
            cs.setInt(count++, light.getOn_off());
            cs.setInt(count++, brightness);
            cs.setString(count++, light.getLight_code());
            cs.setString(count++, light.getLight_code());
            boolean kq = cs.execute();
            if (kq) {
                rs = cs.getResultSet();
                if (rs.next()) {
                    if (rs.getLong(1) > 0 && cs.getMoreResults()) {
                        rs = cs.getResultSet();
                        if (rs.next()) {
                            ret = 0;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("RoomAreaLightModelSQL.updateOnOffByID : " + ex.getMessage(), ex);
        } finally {
            SQLConnFactory.safeClose(rs);
            SQLConnFactory.safeClose(cs);
            SQLConnFactory.safeClose(connection);
        }
        return ret;
    }
}
