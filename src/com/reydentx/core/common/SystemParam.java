/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.common;

/**
 *
 * @author ducnt3
 */
public class SystemParam {
    public static String getRunningMode() {
        String ret = System.getProperty("zappprof");
        
        if(ret == null) {
            ret = "development";
        }
        
        return ret;
    }
}
