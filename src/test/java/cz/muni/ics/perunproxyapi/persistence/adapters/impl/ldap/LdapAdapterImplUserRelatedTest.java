package cz.muni.ics.perunproxyapi.persistence.adapters.impl.ldap;

import cz.muni.ics.perunproxyapi.TestUtils;
import cz.muni.ics.perunproxyapi.persistence.AttributeMappingService;
import cz.muni.ics.perunproxyapi.persistence.adapters.DataAdapter;
import cz.muni.ics.perunproxyapi.persistence.connectors.PerunConnectorLdap;
import cz.muni.ics.perunproxyapi.persistence.connectors.properties.LdapProperties;
import cz.muni.ics.perunproxyapi.persistence.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;


@SpringBootTest
public class LdapAdapterImplUserRelatedTest {
    private static final String USER_LOGIN = "login";

    private User sampleUser;

    @Value("${attributes.identifiers.relying_party}") private String rpIdentifierAttrIdentifier;
    @Value("${attributes.identifiers.additional_identifiers}") private String additionalIdentifiersAttrIdentifier;
    @Value("${attributes.identifiers.login}") private String loginAttrIdentifier;

    private final PerunConnectorLdap connector = mock(PerunConnectorLdap.class);
    private DataAdapter ldapAdapter;
    private LdapProperties ldapProperties;

    @Autowired
    private AttributeMappingService attributeMappingService;

    @BeforeEach
    public void setUp() {
        this.ldapAdapter = new LdapAdapterImpl(connector, this.attributeMappingService, ldapProperties,
                rpIdentifierAttrIdentifier, additionalIdentifiersAttrIdentifier, loginAttrIdentifier);
        this.sampleUser = TestUtils.createSampleUser(USER_LOGIN);
    }

    @Test
    public void getPerunUser() {
        // edupersonprincipalnames
        List<String> uids = new ArrayList<>();

    }

}
