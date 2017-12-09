package com.wahsis.iot.database;
import snaq.db.ConnectionPool;

/*
* @author khiemnv
 */
public class PartnerServer {

    private long partner_server_id;
    private String server_name;
    private String port_number;
    private String database_name;
    private String user_name;
    private String password;
    private int is_default;
    private ConnectionPool pos_pool;
    private String partnerConnString;

    public PartnerServer() {

    }

    public PartnerServer(Long _partner_server_id, String _server_name, String _port_number, String _database_name, String _user_name, String _password, Integer _is_default) {
        partner_server_id = _partner_server_id;
        server_name = _server_name;
        port_number = _port_number;
        database_name = _database_name;
        user_name = _user_name;
        password = _password;
        is_default = _is_default;
        //partnerConnString = "jdbc:sqlserver://" + _server_name + ":" + _port_number + ";databaseName=" + _database_name + ";user=" + _user_name + ";password=" + _password;
        //partner_pool = new ConnectionPool("pos_pool", 5, 20, 60, 180, partnerConnString, _user_name, _password);
    }

    public void initConnectionPool() {
        partnerConnString = "jdbc:sqlserver://" + server_name + ":" + port_number + ";databaseName=" + database_name + ";user=" + user_name + ";password=" + password;
        pos_pool = new ConnectionPool("pos_pool", 5, 20, 60, 180, partnerConnString, user_name, password);
    }

    public ConnectionPool getConnectionPool() {
        return pos_pool;

    }

    public long getPartnerServerId() {
        return partner_server_id;
    }

    public void setPartnerServerId(long _partner_server_id) {
        this.partner_server_id = _partner_server_id;
    }

    public String getServerName() {
        return server_name;
    }

    public void setServerName(String _server_name) {
        this.server_name = _server_name;
    }

    public String getPortNumber() {
        return port_number;
    }

    public void setPortNumber(String _port_number) {
        this.port_number = _port_number;
    }

    public String getDatabaseName() {
        return database_name;
    }

    public void setDatabaseName(String _database_name) {
        this.database_name = _database_name;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String _user_name) {
        this.user_name = _user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String _password) {
        this.password = _password;
    }

    public int getIsDefault() {
        return is_default;
    }

    public void setIsDefault(int _is_default) {
        this.is_default = _is_default;
    }

}
