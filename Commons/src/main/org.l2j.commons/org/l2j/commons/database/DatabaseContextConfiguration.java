package org.l2j.commons.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.l2j.commons.Config;
import org.l2j.commons.database.model.Entity;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.event.AfterLoadEvent;
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent;
import org.springframework.data.relational.core.mapping.event.WithEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
@Import(JdbcConfiguration.class)
@EnableJdbcRepositories({"org.l2j.commons.database", "org.l2j.gameserver.model.entity.database.repository"})
public class DatabaseContextConfiguration {

    @Bean
    public HikariDataSource dataSource() {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.addDataSourceProperty("driverClassName", Config.DATABASE_DRIVER);
        dataSourceConfig.setJdbcUrl(Config.DATABASE_URL);
        dataSourceConfig.setUsername(Config.DATABASE_LOGIN);
        dataSourceConfig.setPassword(Config.DATABASE_PASSWORD);
        dataSourceConfig.addDataSourceProperty("maximumPoolSize", Config.DATABASE_MAX_CONNECTIONS);
        dataSourceConfig.addDataSourceProperty("idleTimeout", Config.DATABASE_MAX_IDLE_TIME); // 0 = idle connections never expire
        dataSourceConfig.addDataSourceProperty("cachePrepStmts", true);
        dataSourceConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        dataSourceConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        dataSourceConfig.addDataSourceProperty("useServerPrepStmts", true);
        dataSourceConfig.addDataSourceProperty("useLocalSessionState", true);
        dataSourceConfig.addDataSourceProperty("useLocalTransactionState", true);
        dataSourceConfig.addDataSourceProperty("rewriteBatchedStatements", true);
        dataSourceConfig.addDataSourceProperty("cacheServerConfiguration", true);
        dataSourceConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        dataSourceConfig.addDataSourceProperty("maintainTimeStats", true);
        dataSourceConfig.addDataSourceProperty("logger", "com.mysql.cj.log.Slf4JLogger");
        dataSourceConfig.addDataSourceProperty("autoCommit", true);
        dataSourceConfig.addDataSourceProperty("minimumIdle", 10);
        dataSourceConfig.addDataSourceProperty("validationTimeout", 500); // 500 milliseconds wait before try to acquire connection again
        dataSourceConfig.addDataSourceProperty("connectionTimeout", 0); // 0 = wait indefinitely for new connection if pool is exhausted
        return new HikariDataSource(dataSourceConfig);
    }

    @Bean
    public NamedParameterJdbcOperations template(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public NamingStrategy namingStrategy() {
        return  new AnnotationNamingStrategy();
    }

    @Bean
    public ApplicationListener<AfterSaveEvent> afterSaveEventApplicationListener() {
        return event -> extractModel(event).ifPresent(Entity::onSave);
    }

    private Optional<Entity> extractModel(WithEntity event) {
        Object entity = event.getEntity();
        if (entity instanceof Entity) {
            return Optional.of((Entity) entity);
        }
        return  Optional.empty();
    }

    @Bean
    public ApplicationListener<AfterLoadEvent> afterLoadEventApplicationListener() {
        return event -> extractModel(event).ifPresent(Entity::onLoad);
    }
}
