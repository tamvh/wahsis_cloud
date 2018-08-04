/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.controller;

import com.wahsis.iot.common.DefinedName;
import com.wahsis.iot.common.MessageType;
import com.wahsis.iot.common.JsonParserUtil;
import com.wahsis.iot.info.ClientSessionInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.wahsis.iot.task.AddLogTask;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author tamvh
 */
@WebSocket
public class DeviceNotifyController {

    private static final Logger logger = Logger.getLogger(DeviceNotifyController.class);
    private static final Gson gson = new Gson();
    private static final Map<String, ClientSessionInfo> _clientSessionMap = Collections.synchronizedMap(new LinkedHashMap<String, ClientSessionInfo>());

    @OnWebSocketConnect
    public void onConnect(Session session) {
        Map<String, List<String>> params = session.getUpgradeRequest().getParameterMap();

        String sessionId = "";
        List<String> sessionIdList = params.get(DefinedName.SESSION_ID);
        if (sessionIdList == null || sessionIdList.isEmpty()) {
            session.close();
            return;
        } else {
            sessionId = sessionIdList.get(0);
        }

        List<HttpCookie> listCookie = new ArrayList<>();
        listCookie.add(new HttpCookie(DefinedName.SESSION_ID, sessionId));
        session.getUpgradeRequest().setCookies(listCookie);

        logger.info("DeviceNotifyController.onConnect: client sessionId = " + sessionId);

        ClientSessionInfo oldClient = _clientSessionMap.get(sessionId);
        System.out.println("Session id: " + sessionId);
        if (oldClient != null) {
            logger.info("DeviceNotifyController.onConnect: close old client sessionId = " + sessionId);
            oldClient.getSession().close();
        }

        _clientSessionMap.put(sessionId, new ClientSessionInfo(session, sessionId));
        session.setIdleTimeout(10 * 60 * 1000);

        return;
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        logger.info("DeviceNotifyController.onClose: remove connect");
        session.close();
        removeSession(session);
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) {
        //samble data: {"msg_type":1, "dt":{"light_code":1, "on_off":0,"brightness":100}}
        logger.info("DeviceNotifyController.onText, Received message from gateway:" + message);
//        logger.info("public ip: " + String.valueOf(session.getUserProperties().get("javax.websocket.endpoint.remoteAddress")));
        JsonObject jsonObject = JsonParserUtil.parseJsonObject(message);
        int msg_type = 0;
        JsonObject json_dt = null;
        JsonObject json_response = null;
        JsonObject dt_response = null;
        if (jsonObject != null) {
            msg_type = jsonObject.get("msg_type").getAsInt();
            //json_dt = jsonObject.get("dt").getAsJsonObject();
            switch (msg_type) {
                case MessageType.MSG_PING:
                    //ping-pong, {"msg_type":1, "dt":{"msg":"ping", "serial_number":"1234"}}
                    json_response = new JsonObject();
                    dt_response = new JsonObject();
                    json_response.addProperty("msg_type", MessageType.MSG_PONG);
                    dt_response.addProperty("msg", "pong");
                    json_response.addProperty("dt", dt_response.toString());
                    logger.info("DeviceNotifyController.onText: send pong data: " + json_response.toString());
                    sendMessageToClient(session, json_response.toString());
                    break;
                case MessageType.MSG_LIGHT_SWITCH_ONOFF:
                    logger.info("DeviceNotifyController.onText: linght switch onoff");
                    AddLogTask.getInstance().addSWitchOnOffMsg(jsonObject.get("dt").toString());
                    break;
                default:
                    break;
            }
        }
        
        
        String my_ping = "PING";
        if (my_ping.compareToIgnoreCase(message) == 0) {
            logger.info("DeviceNotifyController.onText: send pong(valeo)");
            String msg_pong = "PONG";
            sendMessageToClient(session, msg_pong);
        }
    }

    public void removeSession(Session session) {
        List<HttpCookie> listCookie = session.getUpgradeRequest().getCookies();
        for (HttpCookie cookie : listCookie) {
            if (cookie.getName().compareToIgnoreCase(DefinedName.SESSION_ID) == 0) {
                _clientSessionMap.remove(cookie.getValue());
                break;
            }
        }
    }

    public static boolean sendMessageToClient(String sessionId, String data) {
        logger.info("push data to client, ssid: " + sessionId);
        for (Map.Entry<String, ClientSessionInfo> entry : _clientSessionMap.entrySet()) {
            String key = entry.getKey();
            logger.info("key ssid: " + key);
        }
        if (_clientSessionMap.containsKey(sessionId)) {
            ClientSessionInfo clientSession = _clientSessionMap.get(sessionId);
            if (clientSession != null) {
                try {
                    logger.info("DeviceNotifyController.sendMessageToClient: response to client sessionId = " + sessionId + ", data = " + data);
                    clientSession.getSession().getRemote().sendString(data);
                    return true;
                } catch (IOException ex) {
                    logger.error("DeviceNotifyController.sendMessageToClient: " + ex.getMessage(), ex);
                }
            }
        }
        return false;
    }

    public static boolean sendMessageToClient(String data) {
        for (ClientSessionInfo sessionInfo : _clientSessionMap.values()) {
            try {
                sessionInfo.getSession().getRemote().sendString(data);
            } catch (IOException ex) {
                logger.error("DeviceNotifyController.sendMessageToClient: " + ex.getMessage(), ex);
            }
        }
        return true;
    }

    public static boolean sendMessageToClient(Session session, String data) {
        try {
            session.getRemote().sendString(data);
        } catch (IOException ex) {
            logger.error("DeviceNotifyController.sendMessageToClientBySession: " + ex.getMessage(), ex);
        }
        return true;
    }
}
