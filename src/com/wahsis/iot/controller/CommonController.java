/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.wahsis.iot.common.CommonModel;
import com.wahsis.iot.common.JsonParserUtil;
import com.wahsis.iot.common.MessageType;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author tamvh
 */
public class CommonController extends HttpServlet {
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
        logger.info("CommonController, cmd: " + cmd + ", data: " + data);
        switch (cmd) {
            case "get_ip_public":
                content = getIpPublic(data);
        }

        CommonModel.out(content, resp);
    }

    private String getIpPublic(String data) {
        String content;
        int ret = -1;
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            String serial_number = jsonObject.get("serial_number").getAsString();
            String url = jsonObject.get("url").getAsString();
            String protocol = jsonObject.get("protocol").getAsString();
            
            String msg_device_notify;
            JsonObject dt = new JsonObject();
            dt.addProperty("url", url);
            dt.addProperty("protocol", protocol);
            JsonObject dt_push_gw = new JsonObject();

            dt_push_gw.addProperty("msg_type", MessageType.MSG_GET_IP_PUBLIC);
            dt_push_gw.addProperty("dt", _gson.toJson(dt));
            msg_device_notify = _gson.toJson(dt_push_gw);

            //push notify to crestron gateway
            logger.info(getClass().getSimpleName() + ".switchOnOff, serial_number:    " + serial_number);
            logger.info(getClass().getSimpleName() + ".switchOnOff, msg_device_notify: " + msg_device_notify);
            DeviceNotifyController.sendMessageToClient(serial_number, msg_device_notify);
            
            content = CommonModel.FormatResponse(0, "get ip public success");
        } catch (JsonSyntaxException ex) {
            logger.error(getClass().getSimpleName() + ".getIpPublic: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }
        return content;
    }
}
