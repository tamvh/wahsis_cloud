/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.common;

/**
 *  
 * @author thanhnn3
 */
public class MessageType {
    
    public static final int MSG_REQUEST = 0; // message type request from client follow range 0-1000
    public static final int MSG_RESPONSE = 1000; // message type response from server follow range >1000
    
    //define message type of request
    public static final int  MSG_PING = MSG_REQUEST + 1;    
    //define message type of response
    public static final int  MSG_PONG = MSG_RESPONSE + 1;
    
    public static final int MSG_LIGHT_SWITCH_ONOFF= MSG_RESPONSE + 2; 
    public static final int MSG_LIGHT_CHANGE_BRIGHTNESS= MSG_RESPONSE + 3; 
    public static final int MSG_LIGHT_SWITCH_ONOFF_GROUP= MSG_RESPONSE + 4;
    
    
    public static final int MSG_GET_IP_PUBLIC= MSG_RESPONSE + 100;
}