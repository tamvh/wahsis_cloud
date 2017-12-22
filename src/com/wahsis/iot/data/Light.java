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

    public long getLight_id() {
        return light_id;
    }

    public void setLight_id(long light_id) {
        this.light_id = light_id;
    }

    public String getLight_code() {
        return light_code;
    }

    public void setLight_code(String light_code) {
        this.light_code = light_code;
    }

    public String getLight_name() {
        return light_name;
    }

    public void setLight_name(String light_name) {
        this.light_name = light_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getArea_id() {
        return area_id;
    }

    public void setArea_id(long area_id) {
        this.area_id = area_id;
    }

    public int getOn_off() {
        return on_off;
    }

    public void setOn_off(int on_off) {
        this.on_off = on_off;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public int getIs_dim() {
        return is_dim;
    }

    public void setIs_dim(int is_dim) {
        this.is_dim = is_dim;
    }
    
    private long light_id;
    private String light_code;
    private String light_name;
    private String description;
    private long area_id;
    private int on_off;
    private int brightness;
    private int is_active;
    private int is_dim; //0: den onoff, 1: den brightness
}
