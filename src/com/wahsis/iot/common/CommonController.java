/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.common;
import com.google.gson.Gson;
import com.wahsis.iot.data.Light;
import com.wahsis.iot.data.Company;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

/**
 *
 * @author tamvh
 */
public class CommonController {
    private static final Gson _gson = new Gson();
    protected final Logger logger = Logger.getLogger(this.getClass());
    public static void parseData(String data, Light light, Company company) {
        Light light_local = new Light();
        Company company_local = new Company();
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject != null) {
                if (jsonObject.has("light")) {
                    light_local = _gson.fromJson(jsonObject.get("light").getAsJsonObject(), Light.class);
                }
                if (jsonObject.has("company")) {
                    company_local = _gson.fromJson(jsonObject.get("company").getAsJsonObject(), Company.class);
                }
            }
            else {
                light_local = null;
                company_local = null;
            }
            light = light_local;
            company = company_local;
        } catch (Exception ex) {
            light = null;
            company = null;
        }
    }
}
