/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.common;

import com.reydentx.exception.InvalidParamException;
import com.reydentx.exception.NotExistException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author ducnt3
 */
public class HReqParam {

    private static String _getParameterAsString(HttpServletRequest req, String paramName) throws NotExistException, InvalidParamException {
        if (paramName == null) {
            throw new InvalidParamException("Parameter name is null");
        }
        paramName = paramName.trim();
        if (paramName.isEmpty()) {
            throw new InvalidParamException("Parameter name is empty");
        }

        String strVal = req.getParameter(paramName);
        if (strVal == null) {
            throw new NotExistException();
        }
        return strVal;
    }

    ////////////////////////////////////////////////////////////////////////////
    ///specdatatype read with throwing exceptions
    ///
    public static boolean getBoolean(HttpServletRequest req, String paramName) throws NotExistException, NumberFormatException, InvalidParamException {
        String strVal = _getParameterAsString(req, paramName);

        Boolean ret = Boolean.parseBoolean(strVal);
        return ret;
    }

    public static byte getByte(HttpServletRequest req, String paramName) throws NotExistException, NumberFormatException, InvalidParamException {
        return Byte.parseByte(_getParameterAsString(req, paramName));
    }

    public static double getDouble(HttpServletRequest req, String paramName) throws NotExistException, NumberFormatException, InvalidParamException {
        return Double.parseDouble(_getParameterAsString(req, paramName));
    }

    public static float getFloat(HttpServletRequest req, String paramName) throws NotExistException, NumberFormatException, InvalidParamException {
        return Float.parseFloat(_getParameterAsString(req, paramName));
    }

    public static int getInt(HttpServletRequest req, String paramName) throws NotExistException, NumberFormatException, InvalidParamException {
        return Integer.parseInt(_getParameterAsString(req, paramName));
    }

    public static long getLong(HttpServletRequest req, String paramName) throws NotExistException, NumberFormatException, InvalidParamException {
        return Long.parseLong(_getParameterAsString(req, paramName));
    }

    public static short getShort(HttpServletRequest req, String paramName) throws NotExistException, NumberFormatException, InvalidParamException {
        return Short.parseShort(_getParameterAsString(req, paramName));
    }

    public static String getString(HttpServletRequest req, String paramName) throws NotExistException, InvalidParamException {
        return _getParameterAsString(req, paramName);
    }

    ////////////////////////////////////////////////////////////////////////////
    ///specdatatype read without throwing exception (use defaultVal instead)
    ///
    public static boolean getBoolean(HttpServletRequest req, String paramName, Boolean defaultVal) {
        try {
            return getBoolean(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static byte getByte(HttpServletRequest req, String paramName, Byte defaultVal) {
        try {
            return getByte(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static double getDouble(HttpServletRequest req, String paramName, Double defaultVal) {
        try {
            return getDouble(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static float getFloat(HttpServletRequest req, String paramName, Float defaultVal) {
        try {
            return getFloat(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static int getInt(HttpServletRequest req, String paramName, Integer defaultVal) {
        try {
            return getInt(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static long getLong(HttpServletRequest req, String paramName, Long defaultVal) {
        try {
            return getLong(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static short getShort(HttpServletRequest req, String paramName, Short defaultVal) {
        try {
            return getShort(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    public static String getString(HttpServletRequest req, String paramName, String defaultVal) {
        try {
            return getString(req, paramName);
        } catch (Exception ex) {
            return defaultVal;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ///specdatatype read without throwing exception (suffix: ExL - Exception less) (use null instead)
    ///
    public static boolean getBooleanExl(HttpServletRequest req, String paramName) {
        return getBoolean(req, paramName, null);
    }

    public static byte getByteExl(HttpServletRequest req, String paramName) {
        return getByte(req, paramName, null);
    }

    public static double getDoubleExl(HttpServletRequest req, String paramName) {
        return getDouble(req, paramName, null);
    }

    public static float getFloatExl(HttpServletRequest req, String paramName) {
        return getFloat(req, paramName, null);
    }

    public static int getIntExl(HttpServletRequest req, String paramName) {
        return getInt(req, paramName, null);
    }

    public static long getLongExl(HttpServletRequest req, String paramName) {
        return getLong(req, paramName, null);
    }

    public static short getShortExl(HttpServletRequest req, String paramName) {
        return getShort(req, paramName, null);
    }

    public static String getStringExl(HttpServletRequest req, String paramName) {
        return getString(req, paramName, null);
    }

    public static String getStringLastPath(HttpServletRequest req) {
        String path = req.getRequestURI();
        String ret = path.substring(path.lastIndexOf("/") + 1, path.length());
        return ret;
    }

    public static String getStringSecondPath(HttpServletRequest req) {
        String ret = "";
        try {
            String path = req.getRequestURI();
            ret = path.substring(path.indexOf("/", 1) + 1, path.length());
            ret = ret.substring(0, ret.indexOf("/"));
        } catch (Exception ex) {
        }
        return ret;
    }
}