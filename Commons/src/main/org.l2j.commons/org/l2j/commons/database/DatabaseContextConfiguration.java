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
@EnableJdbcRepositories({"org.l2j.commons.database"})
public class DatabaseContextConfiguration {

    @Bean
    public HikariDataSource dataSource() {
        return new HikariDataSource(new HikariConfig());
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
