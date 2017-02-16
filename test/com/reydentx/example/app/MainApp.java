/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.example.app;

import com.reydentx.core.server.RWebServer;
import com.reydentx.example.filter.StaticResourceFilter;
import com.reydentx.example.filter.ExampleFilter2;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;

/**
 *
 * @author ducnt3
 */
public class MainApp {

        public static void main(String[] args) {
                RWebServer.INSTANCE.init(8082, "com.reydentx.example.handler", "/ducnt3")
                        // filters
                        .addFilter(StaticResourceFilter.class, "/*")
                        .addFilter(ExampleFilter2.class, "/*");

                // resource handlers
                ContextHandler resourceHandler = new ContextHandler("/public");
                resourceHandler.setResourceBase("./public/");
                resourceHandler.setHandler(new ResourceHandler());
                
                ContextHandler faviconHandler = new ContextHandler("/favicon.ico");
                faviconHandler.setResourceBase("./public/favicon.ico");
                faviconHandler.setHandler(new ResourceHandler());
                
                RWebServer.INSTANCE.addHandler(resourceHandler);
                RWebServer.INSTANCE.addHandler(faviconHandler);
                
                // start
                RWebServer.INSTANCE.start();
        }
}
