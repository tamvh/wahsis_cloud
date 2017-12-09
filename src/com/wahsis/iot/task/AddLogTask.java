/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.task;

import com.wahsis.iot.controller.LightController;
import com.wahsis.iot.data.Light;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wahsis.iot.common.JsonParserUtil;
import com.wahsis.iot.common.MessageType;
import com.wahsis.iot.data.Company;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
                if (msg != null) {
                    switch (msg.type) {
                        case MessageType.MSG_LIGHT_SWITCH_ONOFF: 
                            JsonObject data = JsonParserUtil.parseJsonObject(msg.data.toString());
                            JsonObject light = data.get("light").getAsJsonObject();
                            JsonObject company = data.get("company").getAsJsonObject();
                            LightController controller = new LightController();
                            controller.updateOnOff(company.get("company_id").toString(), light.toString());
                            break;
                        case MessageType.MSG_LIGHT_CHANGE_BRIGHTNESS:
                            JsonObject data_brn = JsonParserUtil.parseJsonObject(msg.data.toString());
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
                logger.error("NotifyController.sendMessageToClient: " + ex.getMessage(), ex);
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

    public void addSwitchLightMessage(JsonObject data) {
        AddLogMessage msg = new AddLogMessage(MessageType.MSG_LIGHT_SWITCH_ONOFF, data);
        msgQueue.offer(msg);
    }
    
    public void addChangeBrightnessMessage(JsonObject data) {
        AddLogMessage msg = new AddLogMessage(MessageType.MSG_LIGHT_CHANGE_BRIGHTNESS, data);
        msgQueue.offer(msg);
    }

    private class AddLogMessage {
        private int type;
        private Object data;
        public AddLogMessage(int type, Object data) {
            this.type = type;
            this.data = data;
        }
    }
}
