/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Level;

/**
 *
 * @author diepth
 */
public class CommonFunction {
    
    public static String getCurrentDateTime() {
        
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt.setCalendar(cal);
        String currDateTime = fmt.format(cal.getTimeInMillis());
        
        return currDateTime;
    }
    
    public static String formatDateTimeFromString(String date, String format) {
        if (date == null || date.isEmpty()) {
            return "";
        }
        String result = date;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat(format);
            try {
                Date d = sdf.parse(date);
                result = sdf2.format(d);
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(CommonFunction.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e) {
        }

        return result;
    }
    
    public static String formatDateTimeFromString(String date){
        if (date == null || date.isEmpty()) {
            return "";
        }
         //format thành dạng yyyy-MM-dd HH:mm:ss từ chuỗi input (dạng: dd-MM-yyyy HH:mm:ss)
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d = sdf.parse(date);
            result = sdf2.format(d);            
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(CommonFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;        
    }
}
