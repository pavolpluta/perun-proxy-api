package cz.muni.ics.perunproxyapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Separate class for obtaining values from complex data types (e.g. lists) from application.yml config file.
 */
@Configuration
public class ConfigProperties {

    @Bean
    @ConfigurationProperties(prefix = "attributes.paths")
    public List<String> paths() {
        return new ArrayList<>();
    }

}
