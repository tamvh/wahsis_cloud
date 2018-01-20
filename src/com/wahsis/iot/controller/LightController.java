/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.controller;

import com.wahsis.iot.common.CommonModel;
import com.wahsis.iot.common.JsonParserUtil;
import com.wahsis.iot.common.MessageType;
import com.wahsis.iot.data.Light;
import com.wahsis.iot.data.Company;
import com.wahsis.iot.model.LightModel;
import com.wahsis.iot.task.AddLogTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.wahsis.iot.data.CresnetUnit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author diepth
 */
public class LightController extends HttpServlet {

    protected final Logger logger = Logger.getLogger(this.getClass());
    private static final Gson _gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CommonModel.prepareHeader(resp, CommonModel.HEADER_JS);
        resp.setStatus(200);
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp) {
        try {
            processs(req, resp);
        } catch (IOException ex) {
            logger.error(getClass().getSimpleName() + ".handle: " + ex.getMessage(), ex);
        }
    }

    private void processs(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String cmd = req.getParameter("cm") != null ? req.getParameter("cm") : "";
        String data = req.getParameter("dt") != null ? req.getParameter("dt") : "";
        String content = "";

        CommonModel.prepareHeader(resp, CommonModel.HEADER_JS);
        logger.info("LightController, cmd: " + cmd + ", data: " + data);
        switch (cmd) {
            case "switch_on_off":
                content = switchOnOff(data);
                break;
            case "switch_on_off_group":
                content = switchOnOffGroup(data);
                break;
            case "change_brightness":
                content = changeBrightness(data);
                break;
            case "change_brightness_group":
                content = changeBrightnessGroup(data);
                break;
            case "update_data_switch_on_off_from_gateway":
                content = updateDataSwitchOnOff(data);
                break;
            case "update_data_switch_on_off_group_from_gateway":
                content = updateDataSwitchOnOffGroup(data);
                break;
            case "update_data_change_brightness_from_gateway":
                content = updateDataChangeBrightness(data);
                break;
        }

        CommonModel.out(content, resp);
    }

    public String updateDataSwitchOnOff(String data) {
        String content = null;
        int ret = -1;
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {
                Light light = new Light();
                Company company = new Company();
                if (jsonObject.has("light")) {
                    light = _gson.fromJson(jsonObject.get("light").getAsJsonObject(), Light.class);
                }
                if (jsonObject.has("company")) {
                    company = _gson.fromJson(jsonObject.get("company").getAsJsonObject(), Company.class);
                }
                String company_id = company.getCompany_id();
                ret = LightModel.getInstance().updateOnOffByID(company_id, light);
                if (ret == 0) {
                    content = CommonModel.FormatResponse(ret, "update data switch onoff success");
                } else {
                    content = CommonModel.FormatResponse(ret, "update data switch onoff faile");
                }
                JsonObject jsonMain = new JsonObject();
                JsonObject jcontent = new JsonObject();
                jcontent.addProperty("light_code", light.getLight_code());
                jcontent.addProperty("on_off", light.getOn_off());
                jsonMain.addProperty("msg_type", MessageType.MSG_LIGHT_SWITCH_ONOFF);
                jsonMain.add("dt", jcontent);
                String sendData = _gson.toJson(jsonMain);
                logger.info("updateDataSwitchOnOff: data notify: " + sendData);
                NotifyController.sendMessageToClient(sendData);
            }
        } catch (IOException ex) {
            logger.error(getClass().getSimpleName() + ".updateOnOff: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }

        return content;
    }

    public String updateDataSwitchOnOffGroup(String data) {
        String content = null;
        int ret = -1;
        content = CommonModel.FormatResponse(ret, "unknown");
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {
                String list_light_code = "";
                int on_off = -1;
                String company_id = "";
                JsonObject json_light = null;
                Company company = new Company();
                if (jsonObject.has("company")) {
                    company = _gson.fromJson(jsonObject.get("company").getAsJsonObject(), Company.class);
                }

                if (jsonObject.has("light")) {
                    json_light = jsonObject.get("light").getAsJsonObject();
                }

                if (company != null && json_light != null) {
                    company_id = company.getCompany_id();
                    if (json_light.has("list_light_code") && json_light.has("on_off")) {
                        list_light_code = json_light.get("list_light_code").getAsString();
                        on_off = json_light.get("on_off").getAsInt();
                    }
                } else {
                    content = CommonModel.FormatResponse(ret, "Invalid parameter");
                    return content;
                }

                if (list_light_code != "" && company_id != "" && on_off >= 0) {
                    ret = LightModel.getInstance().updateOnOffByGroupId(company_id, list_light_code, on_off);
                    if (ret == 0) {
                        content = CommonModel.FormatResponse(ret, "update data switch onoff group success");
                    } else {
                        content = CommonModel.FormatResponse(ret, "update data switch onoff group faile");
                    }
                } else {
                    content = CommonModel.FormatResponse(ret, "Invalid parameter");
                    return content;
                }

                JsonObject jsonMain = new JsonObject();
                String arr_light_code[] = list_light_code.split(",");
                List<JsonObject> arr_json_data = new ArrayList<>();
                for (int i = 0; i < arr_light_code.length; i++) {
                    JsonObject jsonData = new JsonObject();
                    int brightness = 0;
                    jsonData.addProperty("light_code", arr_light_code[i]);
                    jsonData.addProperty("on_off", on_off);
                    if (on_off == 1) {
                        brightness = 100;
                    }
                    jsonData.addProperty("brightness", brightness);
                    arr_json_data.add(jsonData);
                }

                jsonMain.addProperty("msg_type", MessageType.MSG_LIGHT_SWITCH_ONOFF_GROUP);
                jsonMain.addProperty("dt", arr_json_data.toString());
                String sendData = _gson.toJson(jsonMain);
                logger.info("updateDataSwitchOnOffGroup: data notify: " + sendData);
                NotifyController.sendMessageToClient(sendData);
            }
        } catch (IOException ex) {
            logger.error(getClass().getSimpleName() + ".updateDataSwitchOnOffGroup: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }

        return content;
    }

    public String updateDataChangeBrightness(String data) {
        String content = "";
        int ret = -1;
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {
                Light light = new Light();
                Company company = new Company();
                if (jsonObject.has("light")) {
                    light = _gson.fromJson(jsonObject.get("light").getAsJsonObject(), Light.class);
                }
                if (jsonObject.has("company")) {
                    company = _gson.fromJson(jsonObject.get("company").getAsJsonObject(), Company.class);
                }
                String company_id = company.getCompany_id();
                ret = LightModel.getInstance().updateBrightnessByID(company_id, light);
                if (ret == 0) {
                    content = CommonModel.FormatResponse(ret, "update data when change brightness success");
                } else {
                    content = CommonModel.FormatResponse(ret, "update data when change brightness faile");
                }
                JsonObject jsonMain = new JsonObject();
                JsonObject jsonData = new JsonObject();
                jsonData.addProperty("light_code", light.getLight_code());
                jsonData.addProperty("on_off", light.getOn_off());
                jsonData.addProperty("brightness", light.getBrightness());
                jsonMain.addProperty("msg_type", MessageType.MSG_LIGHT_CHANGE_BRIGHTNESS);
                jsonMain.add("dt", jsonData);
                String sendData = _gson.toJson(jsonMain);

                logger.info("updateDataChangeBrightness: data notify: " + sendData);
                NotifyController.sendMessageToClient(sendData);
            }
        } catch (IOException ex) {
            logger.error(getClass().getSimpleName() + ".updateBrightness: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }

        return content;
    }

    public String switchOnOff(String data) throws IOException {
        String content;
        int ret = -1;
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            Light light = null;
            Company company = null;
            CresnetUnit cunit = null;
            if (jsonObject != null) {
                if (jsonObject.has("light")) {
                    light = _gson.fromJson(jsonObject.get("light").getAsJsonObject(), Light.class);
                }
                if (jsonObject.has("company")) {
                    company = _gson.fromJson(jsonObject.get("company").getAsJsonObject(), Company.class);
                }
                if (jsonObject.has("cunit")) {
                    cunit = _gson.fromJson(jsonObject.get("cunit").getAsJsonObject(), CresnetUnit.class);
                }
            }
            if (light == null || company == null || cunit == null) {
                return CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {
                int on_off = light.getOn_off();
                int is_dim = light.getIs_dim();
                String cunit_serialno = cunit.getSerial_number();
                String light_code = light.getLight_code();
                String company_id = company.getCompany_id();
                if(cunit_serialno.isEmpty() || light_code.isEmpty() || company_id.isEmpty()) {
                    return CommonModel.FormatResponse(ret, "Invalid parameter");
                }
                String msg_device_notify;
                JsonObject dt = new JsonObject();
                dt.addProperty("company_id", company_id);
                dt.addProperty("light_code", light_code);
                dt.addProperty("light_onoff", on_off);
                dt.addProperty("is_dim", is_dim);
                JsonObject dt_push_gw = new JsonObject();

                dt_push_gw.addProperty("msg_type", MessageType.MSG_LIGHT_SWITCH_ONOFF);
                dt_push_gw.addProperty("dt", _gson.toJson(dt));
                msg_device_notify = _gson.toJson(dt_push_gw);

                //push notify to crestron gateway
                DeviceNotifyController.sendMessageToClient(cunit_serialno, msg_device_notify);
                ret = 0;
                content = CommonModel.FormatResponse(ret, "light switch onoff success");
            }
        } catch (JsonSyntaxException ex) {
            logger.error(getClass().getSimpleName() + ".switchOnOff: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }
        return content;
    }

    public String updateOnOff(String company_id, String data) {
        String content = null;
        int ret = -1;
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {
                Light light = new Light();
                light = _gson.fromJson(jsonObject, Light.class);
                logger.info(getClass().getSimpleName() + ".updateOnOff, company_id: " + company_id);
                logger.info(getClass().getSimpleName() + ".updateOnOff, data: " + data);
                ret = LightModel.getInstance().updateOnOffByID(company_id, light);

                JsonObject jsonMain = new JsonObject();
                JsonObject jsonData = new JsonObject();
                jsonData.addProperty("light_code", light.getLight_code());
                jsonData.addProperty("on_off", light.getOn_off());
                if (light.getOn_off() == 0) {
                    light.setBrightness(0);
                } else {
                    light.setBrightness(100);
                }
                jsonData.addProperty("brightness", light.getBrightness());
                jsonMain.addProperty("msg_type", MessageType.MSG_LIGHT_SWITCH_ONOFF);
                jsonMain.add("dt", jsonData);
                String sendData = _gson.toJson(jsonMain);

                NotifyController.sendMessageToClient(sendData);
            }
        } catch (IOException ex) {
            logger.error(getClass().getSimpleName() + ".updateOnOff: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }

        return content;
    }

    public String updateBrightness(String company_id, String data) {
        String content = null;
        int ret = -1;
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {
                Light light = new Light();
                light = _gson.fromJson(jsonObject, Light.class);
                ret = LightModel.getInstance().updateBrightnessByID(company_id, light);

                JsonObject jsonMain = new JsonObject();
                JsonObject jsonData = new JsonObject();
                jsonData.addProperty("light_code", light.getLight_code());
                jsonData.addProperty("on_off", light.getOn_off());
                jsonData.addProperty("brightness", light.getBrightness());
                jsonMain.addProperty("msg_type", MessageType.MSG_LIGHT_CHANGE_BRIGHTNESS);
                jsonMain.add("dt", jsonData);
                String sendData = _gson.toJson(jsonMain);

                NotifyController.sendMessageToClient(sendData);
            }
        } catch (IOException ex) {
            logger.error(getClass().getSimpleName() + ".updateBrightness: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }

        return content;
    }

    private String switchOnOffGroup(String data) {
        String content = null;
        int ret = -1;

        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {
                Company company = new Company();

                if (jsonObject.has("company")) {
                    company = _gson.fromJson(jsonObject.get("company").getAsJsonObject(), Company.class);
                }

                if (jsonObject.has("light")) {
                    JsonObject jsonLightObject = jsonObject.get("light").getAsJsonObject();
                    String list_light_code = "";
                    int value_on_off = -1;
                    if (jsonLightObject.has("list_light_code")) {
                        list_light_code = jsonLightObject.get("list_light_code").getAsString();
                    }

                    if (jsonLightObject.has("on_off")) {
                        value_on_off = jsonLightObject.get("on_off").getAsInt();
                    }

                    if (!list_light_code.isEmpty() && value_on_off >= 0) {
                        String[] arr_light_code = list_light_code.split(",");
                        //push data to crestron gateway
                        String msg_device_notify;
                        String value;
                        JsonObject dt = new JsonObject();
                        dt.addProperty("list_light_code", list_light_code);
                        if (value_on_off == 0) {
                            value = "0";
                        } else {
                            value = String.valueOf(65535);
                        }
                        dt.addProperty("light_onoff", value);
                        JsonObject dt_push_gw = new JsonObject();
                        dt_push_gw.addProperty("cm", "switch_on_off_group");
                        dt_push_gw.addProperty("dt", _gson.toJson(dt));
                        msg_device_notify = _gson.toJson(dt_push_gw);
                        String company_id = company.getCompany_id();
                        DeviceNotifyController.sendMessageToClient(company_id, msg_device_notify);

                        for (String item : arr_light_code) {
                            JsonObject light = new JsonObject();
                            light.addProperty("on_off", value_on_off);
                            if (value_on_off == 0) {
                                light.addProperty("brightness", 0);
                            } else {
                                light.addProperty("brightness", 100);
                            }
                            light.addProperty("light_code", item);

                            //push notify to client
                            JsonObject jsonData = new JsonObject();
                            jsonData.add("light", light);
                            jsonData.add("company", jsonObject.get("company").getAsJsonObject());
//                            AddLogTask.getInstance().addSwitchLightMessage(jsonData);
                        }
                    } else {
                        return CommonModel.FormatResponse(ret, "Invalid parameter");
                    }
                } else {
                    return CommonModel.FormatResponse(ret, "Invalid parameter");
                }

                ret = 0;
                content = CommonModel.FormatResponse(ret, "light switch onoff success");
            }
        } catch (JsonSyntaxException ex) {
            logger.error(getClass().getSimpleName() + ".switchOnOffGroup: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }
        return content;
    }

    private String changeBrightness(String data) {
        String content;
        int ret = -1;

        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {
                Light light = new Light();
                Company company = new Company();
                if (jsonObject.has("light")) {
                    light = _gson.fromJson(jsonObject.get("light").getAsJsonObject(), Light.class);
                }
                if (jsonObject.has("company")) {
                    company = _gson.fromJson(jsonObject.get("company").getAsJsonObject(), Company.class);
                }

                String value;
                String light_code = light.getLight_code();
                String company_id = company.getCompany_id();
                int brightness = light.getBrightness();
                String msg_device_notify;

                value = String.valueOf((int) brightness * 655.35);

                JsonObject dt = new JsonObject();
                dt.addProperty("light_code", light_code);
                dt.addProperty("light_onoff", value);
                JsonObject dt_push_gw = new JsonObject();
                dt_push_gw.addProperty("cm", "change_brightness");
                dt_push_gw.addProperty("dt", _gson.toJson(dt));
                msg_device_notify = _gson.toJson(dt_push_gw);

                //push notify to crestron gateway
                DeviceNotifyController.sendMessageToClient(company_id, msg_device_notify);
                content = CommonModel.FormatResponse(0, "light change brightness success");

                //save to database
                JsonObject light_db = new JsonObject();
                light_db.addProperty("brightness", brightness);
                light_db.addProperty("light_code", light_code);
                JsonObject jsonData = new JsonObject();
                jsonData.add("light", light_db);
                jsonData.add("company", jsonObject.get("company").getAsJsonObject());
                AddLogTask.getInstance().addChangeBrightnessMessage(jsonData);
            }
        } catch (Exception ex) {
            logger.error(getClass().getSimpleName() + ".changeBrightness: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }

        return content;
    }

    private String changeBrightnessGroup(String data) {
        String content = null;
        int ret = -1;

        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {
                Company company = new Company();

                if (jsonObject.has("company")) {
                    company = _gson.fromJson(jsonObject.get("company").getAsJsonObject(), Company.class);
                }

                if (jsonObject.has("light")) {
                    JsonObject jsonLightObject = jsonObject.get("light").getAsJsonObject();
                    String list_light_code = "";
                    int value_brightness = -1;
                    if (jsonLightObject.has("list_light_code")) {
                        list_light_code = jsonLightObject.get("list_light_code").getAsString();
                    }

                    if (jsonLightObject.has("brightness")) {
                        value_brightness = jsonLightObject.get("brightness").getAsInt();
                    }

                    if (!list_light_code.isEmpty() && value_brightness >= 0) {
                        String[] arr_light_code = list_light_code.split(",");
                        //push data to crestron gateway
                        String msg_device_notify;
                        String value;
                        JsonObject dt = new JsonObject();
                        dt.addProperty("list_light_code", list_light_code);
                        value = String.valueOf((int) value_brightness * 655.35);
                        dt.addProperty("brightness", value);
                        JsonObject dt_push_gw = new JsonObject();
                        dt_push_gw.addProperty("cm", "change_brightness_group");
                        dt_push_gw.addProperty("dt", _gson.toJson(dt));
                        msg_device_notify = _gson.toJson(dt_push_gw);
                        String company_id = company.getCompany_id();
                        DeviceNotifyController.sendMessageToClient(company_id, msg_device_notify);

                        for (String item : arr_light_code) {
                            JsonObject light = new JsonObject();
                            light.addProperty("brightness", value_brightness);
                            light.addProperty("light_code", item);

                            //push notify to client
                            JsonObject jsonData = new JsonObject();
                            jsonData.add("light", light);
                            jsonData.add("company", jsonObject.get("company").getAsJsonObject());
                            AddLogTask.getInstance().addChangeBrightnessMessage(jsonData);
                        }
                    } else {
                        return CommonModel.FormatResponse(ret, "Invalid parameter");
                    }
                } else {
                    return CommonModel.FormatResponse(ret, "Invalid parameter");
                }

                ret = 0;
                content = CommonModel.FormatResponse(ret, "light switch onoff success");
            }
        } catch (JsonSyntaxException ex) {
            logger.error(getClass().getSimpleName() + ".switchOnOffGroup: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }
        return content;
    }
}
