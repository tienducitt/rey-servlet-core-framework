/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author ducnt3
 */
public class HttpUtils {
    
    private static final Logger _log = Logger.getLogger(HttpUtils.class);
    
    public static String getETag(HttpServletRequest req) {
        String ifNoneMatchHeader = req.getHeader("If-None-Match");
        return ifNoneMatchHeader != null ? ifNoneMatchHeader.trim() : "";
    }

    public static String getServletURL(HttpServletRequest req) {
        String scheme = req.getScheme(); // http
        String serverName = req.getServerName(); // hostname.com
        int serverPort = req.getServerPort(); // 80
        String contextPath = req.getContextPath(); // /mywebapp
        String servletPath = req.getServletPath(); // /servlet/MyServlet
        String pathInfo = req.getPathInfo(); // /a/b;c=123
        String queryString = req.getQueryString(); // d=789

        // Reconstruct original requesting URL
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath).append(servletPath);

        if (pathInfo != null) {
            url.append(pathInfo);
        }
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }

    public static String getDomain(HttpServletRequest req) {
        String scheme = req.getScheme(); // http
        String serverName = req.getServerName(); // hostname.com
        int serverPort = req.getServerPort(); // 80
        String contextPath = req.getContextPath(); // /mywebapp

        // Reconstruct original requesting URL
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);

        return url.toString();
    }
    
    public static boolean isAjaxRequest(HttpServletRequest request) {
        boolean isAjax = false;
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            isAjax = true;
        }
        return isAjax;
    }
    public static String sendPostRequest(String url, Map<String, String> data) {
        StringBuilder result = new StringBuilder();
        try {
            URL ulrConn = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) ulrConn.openConnection();
            /*
             * Post parameter
             */
            String strData = buildParameter(data);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(strData.length()));
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(strData);
            writer.flush();

            /*
             * read data result
             */
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception ex) {
            _log.error(ex.getMessage() + ex);
        }

        return result.toString();
    }

    public static String sendGetRequest(String url, Map<String, String> data) {
        StringBuilder result = new StringBuilder();
        try {
            String strData = buildParameter(data);
            if (!strData.isEmpty()) {
                url += "?" + strData;
            }
            URL ulrConn = new URL(url);
            URLConnection conn = ulrConn.openConnection();
            /*
             * read data result
             */
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
        } catch (Exception ex) {
            _log.error(ex.getMessage() + ex);
        }

        return result.toString();
    }

    private static String buildParameter(Map<String, String> data) {
        StringBuilder result = new StringBuilder();

        try {
            if (data != null) {
                Set<Map.Entry<String, String>> setEntry = data.entrySet();
                Iterator it = setEntry.iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    result.append("&");
                    result.append(URLEncoder.encode(entry.getKey().toString(), "UTF-8"));
                    result.append("=");
                    result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                }
            }
        } catch (UnsupportedEncodingException ex) {
            _log.error(ex.getMessage() + ex);
        }
        if (result.indexOf("&") > -1) {
            result.deleteCharAt(0);
        }
        return result.toString();
    }
    
    public static String getCookieStartWithDomain(HttpServletRequest req, String name) {
        String ret = getCookie(req, req.getServerName() + "_" + name);
        if("".equals(ret)) {
            ret = getCookie(req, name);
        }
        
        return ret;
    }
    
    public static String getCookie(HttpServletRequest req, String name) {
        String result = "";
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (int i = cookies.length; i > 0; i--) {
                Cookie cookie = cookies[i - 1];
                if (cookie.getName().equals(name)) {
                    result = cookie.getValue();
                    break;
                }
            }
        }
        return result;
    }
    
    public static void setCookie(HttpServletResponse resp, String domain, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setDomain(domain);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }

    public static void removeCookie(HttpServletResponse resp, String domain, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }
}
