/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.common;

import com.reydentx.core.entity.RequestInfo;
import com.reydentx.core.entity.RequestMethod;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author ducnt3
 */
public class ServletUtils {

        public static RequestInfo getRequestInfo(HttpServletRequest req, String contextPath) {
                RequestMethod requestType = getRequestType(req);
                String path = getPath(req, contextPath);
                
                return new RequestInfo(path, requestType);
        }
        
        public static RequestMethod getRequestType(HttpServletRequest req) {
                String method = req.getMethod();
                RequestMethod requestType = RequestMethod.of(method);
                
                return requestType;
        }
        
        public static String getPath(HttpServletRequest req, String rootPath) {
                String path = req.getRequestURI();
                
                return path.toLowerCase();
        }
}
