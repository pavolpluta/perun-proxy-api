package cz.muni.ics.perunproxyapi.persistence.connectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Configuration
@Component
@Slf4j
public class DBConnectors {

    @Bean(name = "proxyApiStats")
    @ConfigurationProperties(prefix = "connector.stats.datasource")
    public DataSource statsDbDataSource() {
        return DataSourceBuilder.create().build();
    }
}
