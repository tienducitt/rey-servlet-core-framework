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
public class Utils {
        public static String replaceAllDupSlash(String input) {
                
                String output = input.replaceAll("//", "/");
                while(!output.equals(input)) {
                        input = output;
                        output = input.replace("//", "/");
                }
                
                return output;
        }
}
