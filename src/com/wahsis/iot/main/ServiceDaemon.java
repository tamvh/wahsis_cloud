/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.main;

import com.wahsis.iot.common.Config;
import com.wahsis.iot.task.AddLogTask;
import org.apache.log4j.Logger;

/**
 *
 * @author haint3
 */
public class ServiceDaemon {
    
    private static final String DEFAULT_CONFIGURATION_FILE = "wahsis_iot_cloud.conf";
    private static final Logger logger = Logger.getLogger(ServiceDaemon.class);
    private static WebServer webServer = null;
    private static ScheduleThread scheduleThread = null;
    
    public static void main(String[] args) {
        try {
            Config.init(DEFAULT_CONFIGURATION_FILE);
            webServer = WebServer.getInstance();
            scheduleThread = new ScheduleThread();
            new Thread(webServer).start();
            new Thread(scheduleThread).start();
            AddLogTask.getInstance().start();
             
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.info("Shutdown thread before webserver getinstance");
                        if (webServer != null) {
                            webServer.stop();
                            AddLogTask.getInstance().stop();
                        }
                    } catch (Exception e) {
                    }
                }
            }, "Stop Jetty Hook"));
        } catch (Throwable e) {
            String msg = "Exception encountered during startup.";
            logger.error(msg, e);
            System.out.println(msg);
            logger.error("Uncaught exception: " + e.getMessage(),e);
            System.exit(3);
        }
    }
}
