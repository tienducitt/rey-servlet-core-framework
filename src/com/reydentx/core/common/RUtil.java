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
public class RUtil {

        public static String replaceAllDupSlash(String input) {

                String output = input.replaceAll("//", "/");
                while (!output.equals(input)) {
                        input = output;
                        output = input.replace("//", "/");
                }

                return output;
        }

        public static boolean parseBooleanWEx(String s) {
                Boolean ret = parseBoolean(s);
                if (ret != null) {
                        return ret;
                } else {
                        throw new NumberFormatException("Wrong format while parsing boolean");
                }
        }

        public static Boolean parseBoolean(String strVal) {
                if (strVal != null) {
                        strVal = strVal.trim();
                        if (strVal.equalsIgnoreCase("true")) {
                                return true;
                        } else if (strVal.equalsIgnoreCase("false")) {
                                return false;
                        } else if (strVal.equalsIgnoreCase("yes")) {
                                return true;
                        } else if (strVal.equalsIgnoreCase("no")) {
                                return false;
                        } else if (strVal.equalsIgnoreCase("on")) {
                                return true;
                        } else if (strVal.equalsIgnoreCase("off")) {
                                return false;
                        } else if (strVal.equalsIgnoreCase("1")) {
                                return true;
                        } else if (strVal.equalsIgnoreCase("0")) {
                                return false;
                        }
                }
                return null;
        }
}
