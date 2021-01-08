package cz.muni.ics.perunproxyapi.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import cz.muni.ics.perunproxyapi.persistence.exceptions.ConfigurationException;
import cz.muni.ics.perunproxyapi.persistence.models.Candidate;
import cz.muni.ics.perunproxyapi.persistence.models.Group;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Utility class for service layer.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
@Slf4j
public class ServiceUtils {

    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String MIDDLE_NAME = "middleName";
    private static final String TITLE_BEFORE = "titleBefore";
    private static final String TITLE_AFTER = "titleAfter";
    public static final String UES_VALUES_SEPARATOR = ";";
    public static final String ATTR_NS_UES = "urn:perun:ues:attribute-def:";

    /**
     * Convert given groupName into the AARC format.
     * @param groupName Name of the group.
     * @param prefix Prefix to be prepended.
     * @param authority Authority issuing the groupName.
     * @return GroupName in the AARC format.
     */
    public static String wrapGroupNameToAARC(@NonNull String groupName, @NonNull String prefix, @NonNull String authority) {
        return prefix + ":group:" + URLEncoder.encode(groupName, StandardCharsets.UTF_8) + '#' + authority;
    }

    /**
     * Convert given capability into the AARC format.
     * @param capability Capability value.
     * @param prefix Prefix to be prepended.
     * @param authority Authority issuing the capability.
     * @return Capability in the AARC format.
     */
    public static String wrapCapabilityToAARC(@NonNull String capability, @NonNull String prefix, @NonNull String authority) {
        return prefix + ':' + capability + '#' + authority;
    }

    /**
     * Get group enitlements for the user.
     * @param groups Groups the user is member of.
     * @param prefix Prefix to be prepended.
     * @param authority Authority issuing the entitlement.
     * @return List of entitlements in the AARC format.
     */
    public static List<String> wrapGroupEntitlements(@NonNull List<Group> groups, @NonNull String prefix,
                                                     @NonNull String authority)
    {
        List<String> entitlements = new ArrayList<>();
        for (Group group : groups) {
            String groupName = group.getUniqueGroupName();
            if (groupName == null) {
                continue;
            }
            groupName = groupName.replaceAll("^(\\w*):members$", "$1");
            groupName = ServiceUtils.wrapGroupNameToAARC(groupName, prefix, authority);
            entitlements.add(groupName);
        }
        Collections.sort(entitlements);
        return entitlements;
    }

    /**
     * Map attributes from request to Candidate object, according to configuration mappers.
     *
     * @param attributes Attributes from request body.
     * @param perunAttributesMapper Map where key is the external attr name and value internal, Perun attr identifier.
     * @param candidateAttributesMapper Map where key is the candidate attr name used in Perun and value is external attr name.
     * @return Candidate object with attributes, without UserExtSoruce.
     */
    public static Candidate createCandidateWithoutUserExtSource(Map<String, JsonNode> attributes,
                                                                Map<String, String> perunAttributesMapper,
                                                                BidiMap<String, String> candidateAttributesMapper)
    {
        Candidate candidate = new Candidate();

        attributes.forEach((attrName, attrValue) -> {
            if (candidateAttributesMapper.containsValue(attrName)) {
                mapCandidateAttribute(candidate, candidateAttributesMapper.getKey(attrName), attrValue.asText());
            } else {
                if (!perunAttributesMapper.containsKey(attrName)) {
                    log.warn("Configuration file does not provide mapping for the attribute '{}'",attrName);
                    return; // behaves as 'continue' in classic for loop
                }
                String perunAttrName = perunAttributesMapper.get(attrName);
                String valStr;
                if (attrValue.isArray()) {
                    StringJoiner sj = new StringJoiner(",");
                    for (JsonNode subVal : attrValue) {
                        sj.add(subVal.textValue());
                    }
                    valStr = sj.toString();
                } else {
                    valStr = attrValue.asText();
                }
                candidate.addAttribute(perunAttrName, valStr);
            }
        });
        return candidate;
    }

    public static JsonNode serializeValueForUes(JsonNode newValue) {
        if (newValue == null || newValue.isNull()) {
            return JsonNodeFactory.instance.nullNode();
        } else if (newValue.isArray()) {
            if (newValue.size() == 0) {
                return JsonNodeFactory.instance.nullNode();
            } else {
                StringJoiner val = new StringJoiner(UES_VALUES_SEPARATOR);
                for (JsonNode node: newValue) {
                    val.add(node.textValue());
                }
                return JsonNodeFactory.instance.textNode(val.toString());
            }
        }

        return newValue;
    }

    /**
     * Map values to default candidate attributes.
     *
     * @param candidate Candidate object
     * @param candidateAttrName Candidate attribute name [firstName | lastName | middleName | titleBefore | titleAfter]
     * @param candidateAttrValue Candidate attribute value
     */
    private static void mapCandidateAttribute(Candidate candidate, String candidateAttrName, String candidateAttrValue) {
        switch (candidateAttrName) {
            case FIRST_NAME:
                candidate.setFirstName(candidateAttrValue);
                break;
            case LAST_NAME:
                candidate.setLastName(candidateAttrValue);
                break;
            case MIDDLE_NAME:
                candidate.setMiddleName(candidateAttrValue);
                break;
            case TITLE_BEFORE:
                candidate.setTitleBefore(candidateAttrValue);
                break;
            case TITLE_AFTER:
                candidate.setTitleAfter(candidateAttrValue);
                break;
            default:
                throw new ConfigurationException(String.format("Invalid mapping key for value %s", candidateAttrValue));
        }
    }

}
