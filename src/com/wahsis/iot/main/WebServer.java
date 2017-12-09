/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.main;

import com.wahsis.iot.common.Config;
import com.wahsis.iot.controller.LightController;
import com.wahsis.iot.controller.NotifyController;
import com.wahsis.iot.controller.DeviceNotifyController;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author diepth
 */
public class WebServer implements Runnable{
    
    private static final Logger logger = Logger.getLogger(WebServer.class);
    private Server server = new Server();
    private static WebServer _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    
    public static WebServer getInstance() {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new WebServer();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    
    @Override
    public void run() {
        try {
            int http_port = Integer.valueOf(Config.getParam("server", "http_port"));
            int ws_port = Integer.valueOf(Config.getParam("server", "ws_port"));
            int ws_port_gateway = Integer.valueOf(Config.getParam("server", "ws_port_gateway"));
            
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(http_port);
            connector.setIdleTimeout(30000);
            
            ServerConnector connectorWS = new ServerConnector(server);
            connectorWS.setPort(ws_port);
            connectorWS.setIdleTimeout(30000);
            
            ServerConnector connectorWS_gateway = new ServerConnector(server);
            connectorWS_gateway.setPort(ws_port_gateway);
            connectorWS_gateway.setIdleTimeout(30000);
            
            server.setConnectors(new Connector[]{connector, connectorWS, connectorWS_gateway});
            logger.info("Start server...");
            
            ServletHandler servletHandler = new ServletHandler();
            servletHandler.addServletWithMapping(LightController.class, "/smart/api/light/*");
            
            ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
            servletContext.setContextPath("/");
            servletContext.setHandler(servletHandler);
            
            WebSocketHandler wsHandler = new WebSocketHandler() {
                @Override
                public void configure(WebSocketServletFactory factory) {
                    factory.register(NotifyController.class);
                }
            };
            ContextHandler wsContextHandler = new ContextHandler();
            wsContextHandler.setHandler(wsHandler);
            wsContextHandler.setContextPath("/smart/ntf/*");
            
            WebSocketHandler wsHandler_gateway = new WebSocketHandler() {
                @Override
                public void configure(WebSocketServletFactory factory) {
                    factory.register(DeviceNotifyController.class);
                }
            };
            ContextHandler wsContextHandler_gateway = new ContextHandler();
            wsContextHandler_gateway.setHandler(wsHandler_gateway);
            wsContextHandler_gateway.setContextPath("/gateway/ntf/*");

            ResourceHandler resource_handler = new ResourceHandler();
            resource_handler.setResourceBase("./static/");
            ContextHandler resourceContext = new ContextHandler();
            resourceContext.setContextPath("/static");
            resourceContext.setHandler(resource_handler);
            
             // Specify the Session ID Manager
            HashSessionIdManager idmanager = new HashSessionIdManager();
            server.setSessionIdManager(idmanager);
            
            // Create the SessionHandler (wrapper) to handle the sessions
            HashSessionManager manager = new HashSessionManager();
            SessionHandler sessions = new SessionHandler(manager);

            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[]{sessions, resourceContext, wsContextHandler, wsContextHandler_gateway, servletContext, new DefaultHandler()});
            server.setHandler(handlers);
            
            server.start();
            server.join();
        } catch (Exception e) {
            logger.error("Cannot start web server: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void stop() throws Exception {
        server.stop();
    }
}
