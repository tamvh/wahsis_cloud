/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.model;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

/**
 *
 * @author tamvh
 */
public class RootAreaModel {
    private static RootAreaModel _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    protected final Logger logger = Logger.getLogger(this.getClass());
   
    public static RootAreaModel getInstance() throws IOException {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new RootAreaModel();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
}
