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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ducnt3
 */
public class ServletMappingContainer {

        private static List<ServletMethod> LIST_HANDLER = new ArrayList<>();

        public static void init(String handlerPackage) {
                Class[] handlers = ClassLoaderUtils.getClasses(handlerPackage);

                for (Class<? extends Object> handler : handlers) {
                        if (handler.isAnnotationPresent(RController.class)) {
                                ServletObject servlet = new ServletObject(handler);

                                for (Method method : handler.getMethods()) {
                                        ServletMethod servletMethod = ServletMethod.newInstance(method, servlet);
                                        if (servletMethod != null) {
                                                LIST_HANDLER.add(servletMethod);
                                                
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
                for (ServletMethod method : LIST_HANDLER) {
                        if (method.getRequestMethod() == reqInfo.getRequestMethod()
                                && reqInfo.getPath().startsWith(method.getFullPath())) {
                                return method;
                        }
                }

                throw new RNotExistHandler(reqInfo.getRequestMethod() + ":" + reqInfo.getPath() + " is not supported");
        }
}
