package org.sorm;

import static org.sorm.EntityManagerConfig.EnvironmentVariables.*;
import static org.sorm.EntityManagerConfig.EnvironmentVariables.DB_URL;
import static org.sorm.EntityManagerConfig.EnvironmentVariables.DB_USERNAME;

public class EntityManagerConfig {
    private final String dbUrl = System.getenv(DB_URL);
    private final String dbUsername = System.getenv(DB_USERNAME);
    private final String dbPassword = System.getenv(DB_PASSWORD);
    private final PrimaryKeyGenerator idGenerator = new PrimaryKeyGenerator();
    private final EntityConverter converter = new EntityConverter();

    public EntityManagerConfig() {
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public PrimaryKeyGenerator getIdGenerator() {
        return idGenerator;
    }

    public EntityConverter getConverter() {
        return converter;
    }

    static class EnvironmentVariables {
        static final String DB_URL = "DB_URL";
        static final String DB_USERNAME = "DB_USERNAME";
        static final String DB_PASSWORD = "DB_PASSWORD";
    }
}
