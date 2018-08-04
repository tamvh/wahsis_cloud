/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.task;

import com.wahsis.iot.controller.LightController;
import com.wahsis.iot.controller.NotifyController;
import com.wahsis.iot.data.Light;
import com.wahsis.iot.data.Company;
import com.wahsis.iot.model.LightModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wahsis.iot.common.JsonParserUtil;
import com.wahsis.iot.common.MessageType;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.apache.log4j.Logger; 
/**
 *
 * @author diepth
 */
public class AddLogTask implements Runnable {

    private static AddLogTask _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    protected final Logger logger = Logger.getLogger(this.getClass());
    private static final Gson _gson = new Gson();
    private static final BlockingQueue<AddLogMessage> msgQueue = new LinkedBlockingQueue<AddLogMessage>();
    private static final int MSG_QUIT = 3;
    private Thread th = null;
    private Boolean start = false;

    public static AddLogTask getInstance() {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new AddLogTask();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }

    @Override
    public void run() {
        AddLogMessage msg = null;
        OUTER:
        while (true) {
            try {
                msg = msgQueue.take();
                logger.info("AddLogTask.run(), msg type: " + msg.type);
                logger.info("AddLogTask.run(), msg data: " + msg.data);
                if (msg != null) {
                    switch (msg.type) {
                        case MessageType.MSG_LIGHT_SWITCH_ONOFF: 
                            int ret_swith_onoff = -1;
                            logger.info("AddLogTask.addSWitchOnOffMsg, begin update database, data: " + msg.data);
                            JsonObject jsonObject = JsonParserUtil.parseJsonObject(msg.data);
                            Light light = new Light();
                            Company company = new Company();
                            if (jsonObject != null) {
                                if (jsonObject.has("light")) {
                                    light = _gson.fromJson(jsonObject.get("light").getAsJsonObject(), Light.class);
                                }
                                if (jsonObject.has("company")) {
                                    company = _gson.fromJson(jsonObject.get("company").getAsJsonObject(), Company.class);
                                }
                            }
                            
                            if (light == null || company == null) {
                                logger.info("AddLogTask.addSWitchOnOffMsg, data null");
                            } else {
                                logger.info("AddLogTask.addSWitchOnOffMsg, company_id: " + company.getCompany_id());
                                logger.info("AddLogTask.addSWitchOnOffMsg, light_id: " + light.getLight_id());
                                logger.info("AddLogTask.addSWitchOnOffMsg, light_code: " + light.getLight_code());
                                if(company.getCompany_id() != null) {
                                    ret_swith_onoff = LightModel.getInstance().updateOnOffByID(company.getCompany_id(), light);
                                    
                                    if(ret_swith_onoff == 0) {
                                        // push data to Notify Client.
                                        JsonObject dt = new JsonObject();
                                        dt.addProperty("light_code", light.getLight_code());
                                        dt.addProperty("on_off", light.getOn_off());
                                        dt.addProperty("brightness", light.getBrightness());
                                        JsonObject dt_push_client = new JsonObject();
                                        dt_push_client.addProperty("msg_type", MessageType.MSG_LIGHT_SWITCH_ONOFF);
                                        dt_push_client.add("dt", dt);
                                        String msg_device_notify = _gson.toJson(dt_push_client);
                                        NotifyController.sendMessageToClient(company.getCompany_id(), msg_device_notify);
                                    }
                                }
                            }
                            break;
                        case MessageType.MSG_LIGHT_CHANGE_BRIGHTNESS:
                            JsonObject data_brn = JsonParserUtil.parseJsonObject(msg.data);
                            JsonObject light_brn = data_brn.get("light").getAsJsonObject();
                            JsonObject company_brn = data_brn.get("company").getAsJsonObject();
                            LightController controller_brn = new LightController();
                            controller_brn.updateBrightness(company_brn.get("company_id").toString(), light_brn.toString());
                        case MSG_QUIT:
                            break OUTER;
                        default:
                            break;
                    }
                }
            } catch (InterruptedException ex) {
                logger.error("AddLogTask Ex: " + ex.getMessage(), ex);
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(AddLogTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void start() {
        if (th == null) {
            start = true;
            th = new Thread(this);
            th.start();
        }
    }

    public void stop() {
        try {
            start = false;
            AddLogMessage msg = new AddLogMessage(MSG_QUIT, null);
            msgQueue.offer(msg);
            th.join();
        } catch (InterruptedException e) {
        }
    }

    public void addSWitchOnOffMsg(String data) {
        logger.info("AddLogTask.addSWitchOnOffMsg: " + data);
        AddLogMessage msg = new AddLogMessage(MessageType.MSG_LIGHT_SWITCH_ONOFF, data);
        msgQueue.offer(msg);
    }
    
    public void addChangeBrightnessMessage(JsonObject data) {
        //AddLogMessage msg = new AddLogMessage(MessageType.MSG_LIGHT_CHANGE_BRIGHTNESS, data);
        //msgQueue.offer(msg);
    }

    private class AddLogMessage {
        private int type;
        private String data;
        public AddLogMessage(int type, String data) {
            this.type = type;
            this.data = data;
        }
    }
}
