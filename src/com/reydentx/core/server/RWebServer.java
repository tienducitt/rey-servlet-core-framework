/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.server;

import com.reydentx.core.container.ServletMappingContainer;
import com.reydentx.core.servlet.CoreHandler;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javafx.util.Pair;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import org.apache.log4j.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 *
 * @author ducnt3
 */
public class RWebServer {

        private static final Logger _LOGGER = Logger.getLogger(RWebServer.class);
        public static RWebServer INSTANCE = new RWebServer();
        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`

        private int port = 80;
        private String handlerPackage;
        private String contextPath;
        private List<Pair<Class<? extends Filter>, String>> filters = new ArrayList<>();
        private List<Handler> handlers = new ArrayList<>();

        public RWebServer init(int port, String handlerPackage, String contextPath) {
                this.port = port;
                this.handlerPackage = handlerPackage;
                this.contextPath = contextPath;
                return this;
        }

        public RWebServer addFilter(Class<? extends Filter> filterClass, String path) {
                filters.add(new Pair<>(filterClass, path));
                return this;
        }

        public RWebServer addHandler(Handler handler) {
                handlers.add(handler);
                return this;
        }

        private RWebServer() {
        }

        public void start() {
                try {
                        Server server = new Server(port);
                        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
                        handler.setContextPath(contextPath);
                        handler.addServlet(CoreHandler.class, "/*");

                        for (Pair<Class<? extends Filter>, String> filter : filters) {
                                handler.addFilter(filter.getKey(), filter.getValue(), EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));
                        }

                        handlers.add(handler);
                        Handler[] arrHandlers = new Handler[handlers.size()];
                        arrHandlers = handlers.toArray(arrHandlers);

                        HandlerList serverHandlers = new HandlerList();
                        serverHandlers.setHandlers(arrHandlers);
                        server.setHandler(serverHandlers);

                        ServletMappingContainer.init(handlerPackage);
                        _LOGGER.info("Web server is ready to serve");
                        server.start();
                } catch (Exception ex) {
                        _LOGGER.error(ex.getMessage(), ex);
                        ex.printStackTrace();
                }
        }

        //~~~~~~~~~~~~ GETTER & SETTER
        public static RWebServer getINSTANCE() {
                return INSTANCE;
        }

        public static void setINSTANCE(RWebServer INSTANCE) {
                RWebServer.INSTANCE = INSTANCE;
        }

        public int getPort() {
                return port;
        }

        public void setPort(int port) {
                this.port = port;
        }

        public String getHandlerPackage() {
                return handlerPackage;
        }

        public void setHandlerPackage(String handlerPackage) {
                this.handlerPackage = handlerPackage;
        }

        public String getContextPath() {
                return contextPath;
        }

        public void setContextPath(String contextPath) {
                this.contextPath = contextPath;
        }

}
