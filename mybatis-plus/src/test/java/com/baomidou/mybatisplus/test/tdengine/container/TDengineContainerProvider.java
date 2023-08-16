package com.baomidou.mybatisplus.test.tdengine.container;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.JdbcDatabaseContainerProvider;
import org.testcontainers.jdbc.ConnectionUrl;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

/**
 * @author Gerrit
 * @since 2023-08-16
 */
public class TDengineContainerProvider extends JdbcDatabaseContainerProvider {

    public static final String USER_PARAM = "user";

    public static final String PASSWORD_PARAM = "password";

    @Override
    public boolean supports(String databaseType) {
        return TDengineContainer.NAME.equals(databaseType);
    }

    @Override
    public JdbcDatabaseContainer newInstance() {
        return newInstance(TDengineContainer.DEFAULT_TAG);
    }

    @Override
    public JdbcDatabaseContainer newInstance(String tag) {
        if (tag != null) {
            return new TDengineContainer(DockerImageName.parse(TDengineContainer.IMAGE).withTag(tag));
        }
        return newInstance(TDengineContainer.DEFAULT_TAG);
    }

    @Override
    protected JdbcDatabaseContainer newInstanceFromConnectionUrl(ConnectionUrl connectionUrl, String userParamName, String pwdParamName) {
        Objects.requireNonNull(connectionUrl, "Connection URL cannot be null");

        final String databaseName = connectionUrl.getDatabaseName().orElse("test");
        final String user = connectionUrl.getQueryParameters().getOrDefault(USER_PARAM, TDengineContainer.DEFAULT_USER);
        final String password = connectionUrl.getQueryParameters().getOrDefault(PASSWORD_PARAM, TDengineContainer.DEFAULT_PASSWORD);

        final JdbcDatabaseContainer<?> instance;
        if (connectionUrl.getImageTag().isPresent()) {
            instance = newInstance(connectionUrl.getImageTag().get());
        } else {
            instance = newInstance();
        }

        JdbcDatabaseContainer<?> container = instance
                .withReuse(connectionUrl.isReusable())
                .withDatabaseName(databaseName)
                .withUsername(user)
                .withPassword(password);
        connectionUrl.getQueryParameters().forEach(container::withUrlParam);
        return container;
    }

    @Override
    public JdbcDatabaseContainer newInstance(ConnectionUrl connectionUrl) {
        return newInstanceFromConnectionUrl(connectionUrl, USER_PARAM, PASSWORD_PARAM);
    }
}
