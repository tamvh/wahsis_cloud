/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.info;

import org.eclipse.jetty.websocket.api.Session;

/**
 *
 * @author diepth
 */
public class ClientSessionInfo {
    
    private Session _session = null;
    private String  _sessionId = "";
    
    public ClientSessionInfo(Session session, String sessionId) {
        _session = session;
        _sessionId = sessionId;
    }
    

    public Session getSession() {
        return _session;
    }

    public void setSession(Session _session) {
        this._session = _session;
    }
    
    public String getSessionId() {
        return _sessionId;
    }

    public void setSessionId(String sessionId) {
        this._sessionId = sessionId;
    }    
}
