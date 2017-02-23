/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.reydentx.core.client;

import com.reydentx.core.config.RConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

/**
 *
 * @author tunt
 */
public class MySQLClient {

    private static final Logger _logger = Logger.getLogger(MySQLClient.class);

    private final String _name;
    private String _host = "localhost";
    private int _port = 3306;
    private String _user = "root";
    private String _password = "123456";
    private String _dbName = "novel";
    private String _url;
    private MySqlClientFactory _clientFactory;
    private int _maxActive;
    private int _maxIdle;
    private long _maxWaitTimeWhenExhausted;
    private ObjectPool<Connection> _pool;
    private int _numRetry = 3;

    public MySQLClient(String name) {
        _name = name;
        _init();
    }

    private void _init() {
        _host = RConfig.Instance.getString(MySQLClient.class, _name, "host", "");
        _port = RConfig.Instance.getInt(MySQLClient.class, _name, "port", 3306);
        _dbName = RConfig.Instance.getString(MySQLClient.class, _name, "dbname", "");
        _user = RConfig.Instance.getString(MySQLClient.class, _name, "user", "");
        _password = RConfig.Instance.getString(MySQLClient.class, _name, "pass", "");
        _url = String.format("jdbc:mysql://%s:%d/%s?interactiveClient=true&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&user=%s&password=%s",
                _host, _port, _dbName, _user, _password);
        _initPool();
    }

    private void _initPool() {
        _clientFactory = new MySqlClientFactory(_url);
        _maxActive = 100;
        _maxIdle = 10;
        _maxWaitTimeWhenExhausted = 5000L;

        GenericObjectPoolConfig poolConf = new GenericObjectPoolConfig();
        poolConf.setMaxIdle(_maxIdle);
        poolConf.setMaxTotal(_maxActive);
        poolConf.setMaxWaitMillis(_maxWaitTimeWhenExhausted);

        _pool = new GenericObjectPool<Connection>(_clientFactory, poolConf);
    }

    public Connection borrowClient() {
        try {
            int retry = _numRetry;
            while (retry > 0) {
                try {
                    return _borrowClient();
                } catch (Exception ex) {
                    --retry;
                }
            }
        } catch (Exception ex) {
            _logger.error("borrowClient: error", ex);
        }
        return null;

    }

    private Connection _borrowClient() throws Exception {
        Connection client = null;
        try {
            client = (Connection) _pool.borrowObject();
            client.setAutoCommit(true);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            _logger.error("_borrowClient: error", ex);
            invalidClient(client);
            throw ex;
        }
        return client;
    }

    public void invalidClient(Connection client) {
        try {
            if (client != null) {
                _pool.invalidateObject(client);
            }
        } catch (Exception ex) {
            _logger.error(ex);
        }
    }

    public void returnObject(Connection client) {
        try {
            _pool.returnObject(client);
        } catch (Exception ex) {
            _logger.error(ex);
        }
    }
    
    public void rollback(Connection client) {
        try {
            client.rollback();
        } catch (SQLException ex) {
            _logger.error(ex.getMessage(), ex);
        }
    }
    
    @Override
    public String toString() {
        return String.format(getClass().getSimpleName() + "[%s] [%s:%d] [%s:%s]",
                _dbName, _host, _port, _user, _password);
    }

    public ResultSet executeQuery(String query) {
        Connection conn = borrowClient();
        if (conn == null) {
            return null;
        }
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            returnObject(conn);
            return rs;
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
            invalidClient(conn);
            return null;
        }
    }

    public int executeUpdate(String query) {
        Connection conn = borrowClient();
        if (conn == null) {
            return -(1);
        }
        try {
            Statement statement = conn.createStatement();
            int ret = statement.executeUpdate(query);
            returnObject(conn);
            return ret;
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
            invalidClient(conn);
            return -(1);
        }
    }

    public int executeMultiQuery(List<String> listQuery) {
        Connection conn = borrowClient();
        if (conn == null) {
            return (-1);
        }
        try {
            conn.setAutoCommit(false);
            for (String query : listQuery) {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.execute();
            }
            conn.commit();
            returnObject(conn);
        } catch (SQLException sqlEx) {
            try {
                _logger.error("Rollback traction because ex:" + sqlEx.getMessage(), sqlEx);
                conn.rollback(); // must be innoDB
            } catch (SQLException sqlExRollback) {
                _logger.error(sqlExRollback.getMessage(), sqlExRollback);
            }
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
            invalidClient(conn);
            return (-1);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception ex) {
                _logger.error(ex.getMessage(), ex);
            }
        }
        return 0;
    }

    public ResultSet excuteStatementQuery(PreparedStatement statement) {
        Connection conn = null;
        try {
            conn = statement.getConnection();
            ResultSet rs = statement.executeQuery();
            _pool.returnObject(conn);
            return rs;
        } catch (Exception ex) {
            _logger.error(ex);
            if (conn != null) {
                invalidClient(conn);
            }
            return null;
        }
    }

    public int excuteStatementUpdate(PreparedStatement statement) {
        Connection conn = null;
        try {
            conn = statement.getConnection();
            int num = statement.executeUpdate();
            _pool.returnObject(conn);
            return num;
        } catch (Exception ex) {
            _logger.error(ex);
            if (conn != null) {
                invalidClient(conn);
            }
            return 0;
        }
    }

    public void close(java.sql.Statement st) {
        try {
            if (st != null && !st.isClosed()) {
                st.close();
            }
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
        }
    }
    
    public void close(java.sql.ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
        }
    }
    
    public void close(java.sql.ResultSet rs, java.sql.Statement st) {
        close(rs);
        close(st);
    }
}
