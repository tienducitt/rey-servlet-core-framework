/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.container;

import com.reydentx.core.annotation.RController;
import org.apache.log4j.Logger;

/**
 *
 * @author ducnt3
 */
public class ServletObject {

        private static final Logger _LOG = Logger.getLogger(ServletObject.class);
        
        private Object servletInstance;
        private String servletPath;
        private Class<? extends Object> handlerClass;
        
        public ServletObject(Class<? extends Object> servletClass) {
                try {
                        
                        this.handlerClass = servletClass;
                        this.servletPath = servletClass.getAnnotation(RController.class).url();
                        this.servletInstance = servletClass.getConstructors()[0].newInstance(new Object[]{});
                } catch (Exception ex) {
                        _LOG.error(ex.getMessage());
                }
        }
        
        // ~~~~~~~~~~~~~~~~~~~~~~ getter & setter
        public Object getServletInstance() {
                return servletInstance;
        }

        public void setServletInstance(Object servletInstance) {
                this.servletInstance = servletInstance;
        }
        
        public String getServletPath() {
                return servletPath;
        }

        public void setServletPath(String servletPath) {
                this.servletPath = servletPath;
        }

        public Class<? extends Object> getHandlerClass() {
                return handlerClass;
        }

        public void setHandlerClass(Class<? extends Object> handlerClass) {
                this.handlerClass = handlerClass;
        }        
}
