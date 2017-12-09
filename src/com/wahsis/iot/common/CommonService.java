/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.common;

/**
 *
 * @author tamvh
 */
public class CommonService {
   public static String getTableName(String company_id, String table_name) {
        String _table_name = "[" + table_name + "]";
        if (company_id != null && company_id.equals("") == false) {
            _table_name = "[" + company_id + "_company].[dbo].[" + table_name + "]";
        }
        return _table_name;
    }

    public static String getStoreName(String company_id, String store_name, String parameter) {
        String _store_name = "exec [" + store_name + "] " + parameter;
        if (company_id != null && company_id.equals("") == false) {
            _store_name = "exec [" + company_id + "_company].[dbo].[" + store_name + "] " + parameter;
        }
        return _store_name;
    }

    public static String getStoreName(String company_id, String store_name, int parameter_number) {
        String parameter = "";
        for (int i = 0; i < parameter_number; i++) {
            if (i == 0) {
                parameter = "?";
            } else {
                parameter = parameter + ",?";
            }
        }
        String _store_name = "exec [" + store_name + "] " + parameter;
        if (company_id != null && company_id.equals("") == false) {
            _store_name = "exec [" + company_id + "_company].[dbo].[" + store_name + "] " + parameter;
        }
        return _store_name;
    } 
}
