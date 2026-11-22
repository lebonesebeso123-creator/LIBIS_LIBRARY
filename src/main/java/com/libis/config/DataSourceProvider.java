package com.libis.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Looks up the container-managed DataSource (declared in META-INF/context.xml) through JNDI.
 * The lookup is done once and cached; Tomcat's pool hands out the connections.
 */
public final class DataSourceProvider {

    public static final String JNDI_NAME = "java:comp/env/jdbc/libis";

    private static volatile DataSource dataSource;

    private DataSourceProvider() {
    }

    public static DataSource getDataSource() {
        DataSource ds = dataSource;
        if (ds == null) {
            synchronized (DataSourceProvider.class) {
                ds = dataSource;
                if (ds == null) {
                    try {
                        ds = (DataSource) new InitialContext().lookup(JNDI_NAME);
                        dataSource = ds;
                    } catch (NamingException e) {
                        throw new IllegalStateException(
                                "DataSource " + JNDI_NAME + " not found. Check META-INF/context.xml.", e);
                    }
                }
            }
        }
        return ds;
    }
}
