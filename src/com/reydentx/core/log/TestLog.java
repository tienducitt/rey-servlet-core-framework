/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.log;

import java.io.File;
import java.net.MalformedURLException;
import org.apache.log4j.Logger;

/**
 *
 * @author Duc
 */
public class TestLog {
        private static final Logger _LOG = Logger.getLogger(TestLog.class);
        
        public static void main(String[] args) throws MalformedURLException {
                System.setProperty("log4j.configuration", new File("conf", "log4j.properties").toURL().toString());
                
                _LOG.info("hello log4J");
                _LOG.debug("debug");
                _LOG.warn("warm");
                _LOG.fatal("fatal");
                _LOG.error("error");
        }
}
