/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.example.app.config;

import com.reydentx.core.client.MySQLClient;
import com.reydentx.core.config.RConfig;

/**
 *
 * @author ducnt3
 */
public class Configuration {
        public static int port = RConfig.Instance.getInt(MySQLClient.class, "main", "port", 0);
}
