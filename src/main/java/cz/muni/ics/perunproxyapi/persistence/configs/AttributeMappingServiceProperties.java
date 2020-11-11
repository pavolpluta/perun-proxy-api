package cz.muni.ics.perunproxyapi.persistence.configs;

import cz.muni.ics.perunproxyapi.persistence.exceptions.ConfigurationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Separate class for obtaining values from complex data types (e.g. lists) from application.yml config file.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Configuration
@ConfigurationProperties(prefix = "attributes")
@Slf4j
public class AttributeMappingServiceProperties {

    @NonNull private List<String> paths = new ArrayList<>();
    @NonNull private Identifiers identifiers;

    @PostConstruct
    public void init() {
        log.info("Initialized AttributeMappingService properties");
        log.debug("{}", this);
    }

    public String getRpIdentifier() {
        return identifiers.getRelyingParty();
    }

    public String getLoginIdentifier() {
        return identifiers.getLogin();
    }

    public String getAdditionalIdentifiersIdentifier() {
        return identifiers.getAdditionalIdentifiers();
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    private static class Identifiers {
        private String relyingParty;
        @NonNull private String login;
        private String additionalIdentifiers;

        public Identifiers(String relyingParty, @NonNull String login, String additionalIdentifiers) {
            this.setRelyingParty(relyingParty);
            this.setLogin(login);
            this.setAdditionalIdentifiers(additionalIdentifiers);
        }

        public void setRelyingParty(String relyingParty) {
            if (!StringUtils.hasText(relyingParty)) {
                this.relyingParty = null;
            } else {
                this.relyingParty = relyingParty;
            }
        }

        public void setLogin(@NonNull String login) {
            if (!StringUtils.hasText(login)) {
                throw new ConfigurationException("Attribute login must not be empty");
            } else {
                this.login = login;
            }
        }

        public void setAdditionalIdentifiers(String additionalIdentifiers) {
            if (!StringUtils.hasText(additionalIdentifiers)) {
                this.additionalIdentifiers = null;
            } else {
                this.additionalIdentifiers = additionalIdentifiers;
            }
        }
    }

}
