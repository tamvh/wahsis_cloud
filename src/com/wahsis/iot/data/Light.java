/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wahsis.iot.data;

/**
 *
 * @author diepth
 */
public class Light {
    
    private long light_id;
    private String light_code;
    private String light_name = "";
    private String description = "";
    private long area_id;
    private int on_off;
    private int status;
    private int brightness;
    private int active;
    private int is_active_brightness;
    
    public int getIsActiveBrightness() {
        return is_active_brightness;
    }

    public void setIsActiveBrightness(int value) {
        is_active_brightness = value;
    }
    public int getActive() {
        return active;
    }
    
    public  void setActive(int active) {
        this.active = active;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }
    /**
     * @return the light_id
     */
    public long getLight_id() {
        return light_id;
    }

    /**
     * @param light_id the light_id to set
     */
    public void setLight_id(long light_id) {
        this.light_id = light_id;
    }

    /**
     * @return the light_code
     */
    public String getLight_code() {
        return light_code;
    }

    /**
     * @param light_code the light_code to set
     */
    public void setLight_code(String light_code) {
        this.light_code = light_code;
    }

    /**
     * @return the light_name
     */
    public String getLight_name() {
        return light_name;
    }

    /**
     * @param light_name the light_name to set
     */
    public void setLight_name(String light_name) {
        this.light_name = light_name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the area_id
     */
    public long getArea_id() {
        return area_id;
    }

    /**
     * @param area_id the area_id to set
     */
    public void setArea_id(long area_id) {
        this.area_id = area_id;
    }

    /**
     * @return the on_off
     */
    public int getOn_off() {
        return on_off;
    }

    /**
     * @param on_off the on_off to set
     */
    public void setOn_off(int on_off) {
        this.on_off = on_off;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }
}
