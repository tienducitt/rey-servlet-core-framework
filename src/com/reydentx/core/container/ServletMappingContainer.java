/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.container;

import com.reydentx.core.annotation.RController;
import com.reydentx.core.common.ClassLoaderUtils;
import com.reydentx.core.entity.RequestInfo;
import com.reydentx.core.exception.RNotExistHandler;
import java.lang.reflect.Method;
import org.eclipse.jetty.http.PathMap;

/**
 *
 * @author ducnt3
 */
public class ServletMappingContainer {

        private static PathMap GET_MAP = new PathMap();
        private static PathMap POST_MAP = new PathMap();

        public static void init(String handlerPackage) {
                Class[] handlers = ClassLoaderUtils.getClasses(handlerPackage);

                for (Class<? extends Object> handler : handlers) {
                        if (handler.isAnnotationPresent(RController.class)) {
                                ServletObject servlet = new ServletObject(handler);

                                for (Method method : handler.getDeclaredMethods()) {
                                        ServletMethod servletMethod = ServletMethod.newInstance(method, servlet);
                                        
                                        if (servletMethod != null) {
                                                switch (servletMethod.getRequestMethod()) {
                                                        case GET: {
                                                                GET_MAP.put(servletMethod.getFullPath(), servletMethod);
                                                                break;
                                                        }
                                                        case POST: {
                                                                POST_MAP.put(servletMethod.getFullPath(), servletMethod);
                                                                break;
                                                        }
                                                }
                                                
                                                System.out.println(servletMethod.getRequestMethod() 
                                                        + ": " + servletMethod.getFullPath()
                                                        + "\n\t-> " + servletMethod.getServletObject().getHandlerClass() + "."
                                                        + servletMethod.getMethod().getName() + "()");
                                        }
                                }
                        }
                }
        }

        public static ServletMethod getServletMethod(RequestInfo reqInfo) throws RNotExistHandler {
                ServletMethod ret = null;
                switch(reqInfo.getRequestMethod()) {
                        case GET: {
                                ret = (ServletMethod) GET_MAP.match(reqInfo.getPath());
                                break;
                        } 
                        case POST: {
                                ret = (ServletMethod) POST_MAP.match(reqInfo.getPath());
                                break;
                        }
                }
                if(ret != null) {
                        return ret;
                }

                throw new RNotExistHandler(reqInfo.getRequestMethod() + ":" + reqInfo.getPath() + " is not supported");
        }
}
