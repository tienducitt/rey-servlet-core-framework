/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.entity;

/**
 *
 * @author ducnt3
 */
public enum RequestMethod {

        GET,POST;
        
        public static RequestMethod of(String string) {
                RequestMethod ret = GET;
                try {
                        ret = valueOf(string);
                } catch (Exception ex) {
                }
                return ret;
        }
}
