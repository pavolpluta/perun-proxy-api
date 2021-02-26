package cz.muni.ics.perunproxyapi.persistence.connectors;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import cz.muni.ics.perunproxyapi.persistence.connectors.properties.StatisticsDbProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@Slf4j
public class DatabaseBeans {

    @Bean
    @Autowired
    public DataSource statisticsDataSource(StatisticsDbProperties dbProperties) {
        HikariConfig configuration = new HikariConfig();
        configuration.setJdbcUrl(dbProperties.getUrl());
        configuration.setDriverClassName(dbProperties.getDriverClassName());
        configuration.setUsername(dbProperties.getUsername());
        configuration.setPassword(dbProperties.getPassword());
        configuration.setMaximumPoolSize(dbProperties.getMaximumPoolSize());
        return new HikariDataSource(configuration);
    }

    @Bean
    @Autowired
    public NamedParameterJdbcTemplate statisticsJdbcTemplate(@Qualifier("statisticsDataSource") DataSource ds) {
        return new NamedParameterJdbcTemplate(ds);
    }

}
