package cz.muni.ics.perunproxyapi.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import cz.muni.ics.perunproxyapi.persistence.configs.AttributeMappingServiceProperties;
import cz.muni.ics.perunproxyapi.persistence.models.AttributeObjectMapping;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service providing methods to use AttributeObjectMapping objects when fetching attributes.
 *
 * Attributes are listed in a separate .yml file in the following way:
 *  - identifier: identifier1
 *    rpcName: rpcName1
 *    ldapName: ldapName1
 *    attrType: type1
 *  - identifier: identifier2
 *    rpcName: rpcName2
 *    ldapName: ldapName2
 *    attrType: type2
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 * @author Dominik Baranek <baranek@ics.muni.cz>
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Component
@Slf4j
public class AttributeMappingService {

    private final Map<String, AttributeObjectMapping> attributeMap = new HashMap<>();
    private final List<String> paths = new ArrayList<>();

    @Autowired
    public AttributeMappingService(@NonNull AttributeMappingServiceProperties attributeMappingServiceProperties) {
        this.paths.addAll(attributeMappingServiceProperties.getPaths());
    }

    /**
     * Initializes attributes and stores them in attributeMap property.
     */
    @PostConstruct
    public void postInit() {
        if (!paths.isEmpty()) {
            initAttrMappings(paths);
        } else {
            log.warn("No path for AttributeMapping file given, no mappings initialized");
        }
    }

    /**
     * Finds AttributeObjectMapping object by attribute identifier.
     *
     * @param identifier String identifier of attribute
     * @return AttributeObjectMapping attribute
     */
    public AttributeObjectMapping getMappingByIdentifier(String identifier) {
        return getMappingByIdentifier(identifier, true);
    }

    /**
     * Finds AttributeObjectMapping object by attribute identifier.
     *
     * @param identifier String identifier of attribute
     * @return AttributeObjectMapping attribute
     */
    public AttributeObjectMapping getMappingByIdentifier(String identifier, boolean throwExc) {
        if (identifier == null || !attributeMap.containsKey(identifier)) {
            if (throwExc) {
                log.error("Could not fetch mapping for identifier {}. Check your attribute mapping file.", identifier);
                throw new IllegalArgumentException("Unknown identifier " + identifier + " check your configuration");
            } else {
                log.warn("Could not fetch mapping for identifier {}. Check your attribute mapping file. Returning null",
                        identifier);
                return null;
            }
        }

        return attributeMap.get(identifier);
    }

    /**
     * Finds AttributeObjectMapping objects by collection of attribute identifiers.
     *
     * @param identifiers Collection of Strings identifiers of attributes
     * @return Set of AttributeObjectMapping objects
     */
    public Set<AttributeObjectMapping> getMappingsByIdentifiers(Collection<String> identifiers) {
        Set<AttributeObjectMapping> mappings = new HashSet<>();
        if (identifiers != null) {
            for (String identifier : identifiers) {
                mappings.add(getMappingByIdentifier(identifier, false));
            }
        }

        return mappings;
    }

    /**
     * Handles initialization of attributes into attributeMap.
     *
     * @param paths List of string paths to files with attributes
     */
    private void initAttrMappings(List<String> paths) {
        for (String path: paths) {
            try {
                List<AttributeObjectMapping> attrsMapping = getAttributesFromYamlFile(path);
                log.trace("Reading attributes from file '{}'", path);

                if (attrsMapping != null) {
                    for (AttributeObjectMapping aom : attrsMapping) {
                        if (aom.getLdapName() != null && aom.getLdapName().trim().isEmpty()) {
                            aom.setLdapName(null);
                        }
                        attributeMap.put(aom.getIdentifier(), aom);
                    }
                }
                log.trace("Attributes from config file '{}' were initialized", path);

            } catch (IOException ex) {
                log.warn("Reading attributes from config file '{}' was not successful.", path);
            }
        }

        log.trace("All attributes were initialized successfully.");
    }

    /**
     * Reads YAML file and map it into AttributeMappingFromYAML object.
     *
     * @param path String path to YAML file with attributes
     * @return AttributesMappingFromYAML object with mapped attributes
     * @throws IOException thrown when file does not exist, is empty or does not have the right structure
     */
    private List<AttributeObjectMapping> getAttributesFromYamlFile(String path) throws IOException {
        return new ObjectMapper(new YAMLFactory()).readValue(new File(path), new TypeReference<>() {});
    }

}
