/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.container;

import com.reydentx.core.annotation.RResponseBody;
import com.reydentx.core.annotation.RServletMapping;
import com.reydentx.core.common.RUtil;
import com.reydentx.core.entity.RequestMethod;
import com.reydentx.core.server.RWebServer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author ducnt3
 */
public class ServletMethod {

        private ServletObject servletObject;
        
        private RequestMethod requestMethod;
        private String url;
        private String fullPath;
        private Method method;
        private boolean responseBody;

        public static ServletMethod newInstance(Method method, ServletObject servletObject) {
                ServletMethod ret = null;

                if (method.isAnnotationPresent(RServletMapping.class)) {
                        ret = new ServletMethod();
                        ret.method = method;
                        ret.servletObject = servletObject;
                        ret.requestMethod = method.getAnnotation(RServletMapping.class).method();
                        ret.url = method.getAnnotation(RServletMapping.class).url().toLowerCase();
                        ret.responseBody = method.isAnnotationPresent(RResponseBody.class);
                        ret.buildFullPath();
                }

                return ret;
        }
        
        public void buildFullPath() {
                String fp = RWebServer.getINSTANCE().getContextPath() + "/" + servletObject.getServletPath() + url;
                fp = RUtil.replaceAllDupSlash(fp);
                this.fullPath = fp;
        }
        
        public Object invoke(Object... param) throws Throwable {
                try {
                return method.invoke(servletObject.getServletInstance(), param);
                } catch (InvocationTargetException ex) {
                        throw ex.getCause();
                }
        }

        // ~~~~~~~~~~~~ getter & setter ~~~~~~~~~~~~~~~
        public RequestMethod getRequestMethod() {
                return requestMethod;
        }

        public void setRequestMethod(RequestMethod requestType) {
                this.requestMethod = requestType;
        }

        public String getUrl() {
                return url;
        }

        public void setUrl(String url) {
                this.url = url;
        }

        public String getFullPath() {
                return fullPath;
        }

        public void setFullPath(String fullPath) {
                this.fullPath = fullPath;
        }

        public Method getMethod() {
                return method;
        }

        public void setMethod(Method method) {
                this.method = method;
        }

        public ServletObject getServletObject() {
                return servletObject;
        }

        public void setServletObject(ServletObject servletObject) {
                this.servletObject = servletObject;
        }

        public boolean isResponseBody() {
                return responseBody;
        }

        public void setResponseBody(boolean responseBody) {
                this.responseBody = responseBody;
        }
        
        
}
