/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.main;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author huytam
 */
public class ScheduleThread implements Runnable{
    private static ScheduleThread _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    public static ScheduleThread getInstance() {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new ScheduleThread();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    
    @Override
    public void run() {
        // do something
        System.err.println("");
    }
}
