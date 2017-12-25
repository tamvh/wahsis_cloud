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
import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author diepth
 */
@WebSocket
public class NotifyController {

    private static final Logger logger = Logger.getLogger(NotifyController.class);
    private static final Gson gson = new Gson();
    private static final Map<String, ClientSessionInfo> _clientSessionMap = Collections.synchronizedMap(new LinkedHashMap<String, ClientSessionInfo>());

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException {
        Map<String, List<String>> params = session.getUpgradeRequest().getParameterMap();

        String uuid = "";
        String company_id = "";
        String session_id = "";
        List<String> uuidList = params.get(DefinedName.UUID);
        if (uuidList == null || uuidList.isEmpty()) {
            session.close();
            return;
        } else {
            uuid = uuidList.get(0);
        }

        List<String> companyIdList = params.get(DefinedName.COMPANY_ID);
        if (companyIdList == null || companyIdList.isEmpty()) {
            session.close();
            return;
        } else {
            company_id = companyIdList.get(0);
        }

        session_id = company_id + "@@" + uuid;

        List<HttpCookie> listCookie = new ArrayList<>();
        listCookie.add(new HttpCookie(DefinedName.SESSION_ID, session_id));
        session.getUpgradeRequest().setCookies(listCookie);

        logger.info("NotifyController.onConnect: client sessionId = " + session_id);

        ClientSessionInfo oldClient = _clientSessionMap.get(session_id);
        System.out.println("Session id: " + session_id);
        if (oldClient != null) {
            logger.info("DeviceNotifyController.onConnect: close old client sessionId = " + session_id);
            oldClient.getSession().close();
        }

        _clientSessionMap.put(session_id, new ClientSessionInfo(session, session_id));
        session.setIdleTimeout(10 * 60 * 1000);
        return;
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        logger.info("NotifyController.onClose: remove connect");
        session.close();
        removeSession(session);
    }

    @OnWebSocketMessage
    public void onText(Session session, String message) {
        logger.info("Received message:" + message);
        JsonObject jsonReq = JsonParserUtil.parseJsonObject(message);
        if (jsonReq != null && jsonReq.has("msg_type")) {
            if (jsonReq.get("msg_type").getAsInt() == MessageType.MSG_PING) {
                JsonObject jsonResp = new JsonObject();

                jsonResp.addProperty("msg_type", MessageType.MSG_PONG);
                jsonResp.addProperty("dt", "Hi! This is pong message response from server");
                String sendMsgPong = gson.toJson(jsonResp);
                sendMessageToClient(session, sendMsgPong);
            }
        }
    }

    public void removeSession(Session session) {
        List<HttpCookie> listCookie = session.getUpgradeRequest().getCookies();
        for (HttpCookie cookie : listCookie) {
            if (cookie.getName().compareToIgnoreCase(DefinedName.SESSION_ID) == 0) {
                logger.info("remove session: " + cookie.getValue());
                _clientSessionMap.remove(cookie.getValue());
                break;
            }
        }
    }

    public static boolean sendMessageToClient(String company_id, String data) throws IOException {
        Set<String> keys = _clientSessionMap.keySet();
        for (String key : keys) {
            if (key.split("@@")[0].compareTo(company_id) == 0) {
                ClientSessionInfo clientSession = _clientSessionMap.get(key);
                clientSession.getSession().getRemote().sendString(data);
            }
        }
        return false;
    }

    public static boolean sendMessageToClient(String data) {
//        for (ClientSessionInfo sessionInfo: _clientSessionMap.values()) {
//            try {
//                sessionInfo.getSession().getRemote().sendString(data);
//            } catch (IOException ex) {
//                logger.error("NotifyController.sendMessageToClient: " + ex.getMessage(), ex);
//            }
//        }
        return true;
    }

    public static boolean sendMessageToClient(Session session, String data) {
        try {
            session.getRemote().sendString(data);
        } catch (IOException ex) {
            logger.error("NotifyController.sendMessageToClientBySession: " + ex.getMessage(), ex);
        }
        return true;
    }
}
