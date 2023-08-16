package com.baomidou.mybatisplus.test.tdengine.container;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.taosdata.jdbc.TSDBDriver;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

/**
 * @author Gerrit
 * @since 2023-08-16
 */
public class TDengineContainer<SELF extends JdbcDatabaseContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

    public static final String NAME = "tdengine";

    public static final String IMAGE = "tdengine/tdengine";

    public static final String DEFAULT_TAG = "3.1.0.0";

    public static final int REST_PORT = 6041;

    public static final String DRIVER_NAME = "com.taosdata.jdbc.rs.RestfulDriver";

    public static final String DEFAULT_USER = "root";

    public static final String DEFAULT_PASSWORD = "taosdata";

    private String databaseName = "test";

    private String username = DEFAULT_USER;

    private String password = DEFAULT_PASSWORD;

    public TDengineContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        try {
            Class.forName(DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.waitStrategy = new LogMessageWaitStrategy()
                .withRegEx(".*succeed to write dnode file:/var/lib/taos//dnode/dnode.json, num:1 ver:2.*")
                .withTimes(2)
                .withStartupTimeout(Duration.of(60, ChronoUnit.SECONDS));
        addExposedPort(REST_PORT);
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        Properties connProps = new Properties();
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_USER, username);
        connProps.setProperty(TSDBDriver.PROPERTY_KEY_PASSWORD, password);
        try (Connection conn = DriverManager.getConnection(getJdbcUrlWithoutDatabase(), connProps);
             Statement stmt = conn.createStatement()) {
            // 建库
            stmt.executeUpdate(String.format("create database %s cachemodel 'both'", databaseName));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        super.containerIsStarted(containerInfo);
    }

    @Override
    public String getDriverClassName() {
        return DRIVER_NAME;
    }

    private String getJdbcUrlWithoutDatabase() {
        String additionalUrlParams = constructUrlParameters("?", "&");
        return String.format("jdbc:TAOS-RS://%s:%s%s", getHost(), getMappedPort(REST_PORT), additionalUrlParams);
    }

    @Override
    public String getJdbcUrl() {
        String additionalUrlParams = constructUrlParameters("?", "&");
        return String.format("jdbc:TAOS-RS://%s:%s/%s%s", getHost(), getMappedPort(REST_PORT), databaseName, additionalUrlParams);
    }

    @Override
    public String getUsername() {
        return DEFAULT_USER;
    }

    @Override
    public String getPassword() {
        return DEFAULT_PASSWORD;
    }

    @Override
    protected String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    public SELF withDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return self();
    }

    @Override
    public SELF withUsername(String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(String password) {
        this.password = password;
        return self();
    }
}
