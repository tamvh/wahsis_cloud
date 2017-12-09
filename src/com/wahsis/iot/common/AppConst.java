/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.common;

/**
 *
 * @author haint3
 */
public class AppConst {
    public static final String  TOPIC_FORMAT    = "%s/devices/%s/%s/%s";
    public static final int     TURN_OFF        = 0;
    public static final int     TURN_ON         = 1;
    
    public static final int DEVICE_GATEWAY = 1;
    public static final int DEVICE_READER = 2;
    public static final int DEVICE_DOOR = 3;
    public static final int DEVICE_LIGHT = 4;
    public static final int DEVICE_TAG = 5;
}
