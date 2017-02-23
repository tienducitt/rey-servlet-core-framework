/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.client;

import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 *
 * @author ducnt3
 */
class MySqlClientFactory extends BasePooledObjectFactory<Connection>{
    
    private String url;
    
    public MySqlClientFactory(String url) {
        this.url = url;
    }
    
    @Override
    public Connection create() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(url);
    }

    @Override
    public PooledObject<Connection> wrap(Connection t) {
        return new DefaultPooledObject<>(t);
    }
}
