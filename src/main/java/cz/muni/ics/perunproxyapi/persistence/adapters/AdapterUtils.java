package cz.muni.ics.perunproxyapi.persistence.adapters;

import cz.muni.ics.perunproxyapi.persistence.adapters.impl.ldap.LdapAdapterImpl;
import cz.muni.ics.perunproxyapi.persistence.exceptions.PerunConnectionException;
import cz.muni.ics.perunproxyapi.persistence.exceptions.PerunUnknownException;
import cz.muni.ics.perunproxyapi.persistence.models.AttributeObjectMapping;
import cz.muni.ics.perunproxyapi.persistence.models.PerunAttributeValue;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cz.muni.ics.perunproxyapi.persistence.adapters.impl.ldap.LdapAdapterImpl.PERUN_GROUP_ID;
import static cz.muni.ics.perunproxyapi.persistence.adapters.impl.ldap.LdapAdapterImpl.PERUN_VO_ID;
import static cz.muni.ics.perunproxyapi.persistence.enums.Entity.FACILITY;
import static cz.muni.ics.perunproxyapi.persistence.enums.Entity.USER;

/**
 * Utility class for adapters.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
@Slf4j
public class AdapterUtils {

    /**
     * Extract forwarded entitlements.
     * @param dataAdapter Adapter to be used.
     * @param userId Perun ID of user.
     * @param entitlementsIdentifier Identifier of the attribute containing the forwarded entitlements. Pass NULL
     *                               or empty String to ignore the forwarded entitlements.
     * @return List of entitlements (filled or empty).
     * @throws PerunUnknownException Thrown as wrapper of unknown exception thrown by Perun interface.
     * @throws PerunConnectionException Thrown when problem with connection to Perun interface occurs.
     */
    public static List<String> getForwardedEntitlements(@NonNull DataAdapter dataAdapter,
                                                        @NonNull Long userId,
                                                        String entitlementsIdentifier)
            throws PerunUnknownException, PerunConnectionException
    {
        if (!StringUtils.hasText(entitlementsIdentifier)) {
            return new ArrayList<>();
        }

        PerunAttributeValue attributeValue = dataAdapter.getAttributeValue(USER, userId, entitlementsIdentifier);
        if (attributeValue != null && attributeValue.valueAsList() != null) {
            return attributeValue.valueAsList();
        }

        return new ArrayList<>();
    }

    /**
     * Extract LDAP name from the mapping. Name is considered as required.
     * @param mapping Mapping object.
     * @return Extracted name. If the mapping is null or LDAP name is empty, an exception is thrown.
     */
    public static String getRequiredLdapNameFromMapping(AttributeObjectMapping mapping) {
        if (mapping == null || !StringUtils.hasText(mapping.getLdapName())) {
            log.error("Name of the LDAP attribute is unknown - mapping:{}. Fix the configuration", mapping);
            throw new IllegalArgumentException("Name of the attribute in LDAP is required.");
        }

        return mapping.getLdapName();
    }

    /**
     * Extract LDAP name from the mapping. Name is considered as required.
     * @param mapping Mapping object.
     * @return Extracted name. If the mapping is null or LDAP name is empty, an exception is thrown.
     */
    public static String getRequiredRpcNameFromMapping(AttributeObjectMapping mapping) {
        if (mapping == null || !StringUtils.hasText(mapping.getRpcName())) {
            log.error("Name of the RPC attribute is unknown - mapping:{}. Fix the configuration", mapping);
            throw new IllegalArgumentException("Name of the attribute in RPC is required.");
        }

        return mapping.getRpcName();
    }

    /**
     * Extract checkGroupAttribute attribute from the facility.
     * @param dataAdapter Adapter to be used.
     * @param facilityId Facility id.
     * @param checkGroupMembershipAttrIdentifier checkGroupMembership attribute.
     * @return Boolean value of the checkGroupMembership attribute. If attribute does not exist, returns FALSE.
     * @throws PerunUnknownException Thrown as wrapper of unknown exception thrown by Perun interface.
     * @throws PerunConnectionException Thrown when problem with connection to Perun interface occurs.
     */
    public static boolean checkGroupMembership(@NonNull DataAdapter dataAdapter,
                                               @NonNull Long facilityId,
                                               String checkGroupMembershipAttrIdentifier)
            throws PerunUnknownException, PerunConnectionException{
        if (!StringUtils.hasText(checkGroupMembershipAttrIdentifier)) {
            return false;
        }

        PerunAttributeValue attributeValue = dataAdapter.getAttributeValue(FACILITY, facilityId, checkGroupMembershipAttrIdentifier);
        if (attributeValue != null) {
            return attributeValue.valueAsBoolean();
        }

        return false;
    }

    /**
     * Extract isTestSp attribute from the facility.
     * @param dataAdapter Adapter to be used.
     * @param facilityId Facility id.
     * @param isTestSpIdentifier Identifier of the isTestSp attribute.
     * @return TRUE if facility is test service provider. If facility is production SP, return FALSE.
     * @throws PerunUnknownException Thrown as wrapper of unknown exception thrown by Perun interface.
     * @throws PerunConnectionException Thrown when problem with connection to Perun interface occurs.
     */
    public static boolean isTestSp(DataAdapter dataAdapter, Long facilityId, String isTestSpIdentifier)
            throws PerunUnknownException, PerunConnectionException {
        if (!StringUtils.hasText(isTestSpIdentifier)) {
            return false;
        }

        PerunAttributeValue attributeValue = dataAdapter.getAttributeValue(FACILITY, facilityId, isTestSpIdentifier);
        if (attributeValue != null) {
            return attributeValue.valueAsBoolean();
        }

        return false;
    }

    /**
     * Extract perunGroupIds which given user is member of.
     * @param ldapAdapter LDAP adapter.
     * @param userId Id of the user.
     * @param memberOfIdentifier LDAP identifier of memberOf attribute.
     * @return Set of group ids (filled or empty).
     */
    public static Set<Long> getLdapGroupIdsWhereUserIsMember(LdapAdapterImpl ldapAdapter, Long userId, String memberOfIdentifier)
    {
        if (!StringUtils.hasText(memberOfIdentifier)){
            return new HashSet<>();
        }

        PerunAttributeValue attributeValue = ldapAdapter.getAttributeValue(USER, userId, memberOfIdentifier);
        if (attributeValue != null && attributeValue.valueAsList() != null) {
            Set<Long> groupIds = new HashSet<>();
            for (String memberOfValue: attributeValue.valueAsList()) {
                String groupId = memberOfValue.split(",",2)[0];
                groupId = groupId.replace(PERUN_GROUP_ID + '=',"");
                groupIds.add(Long.parseLong(groupId));
            }
            return groupIds;
        }

        return new HashSet<>();
    }

    /**
     * Extract perunVoIds which given user is member of.
     * @param ldapAdapter LDAP adapter.
     * @param userId Id of the user.
     * @param memberOfIdentifier LDAP identifier of memberOf attribute.
     * @return Set of VO ids (filled or empty).
     */
    public static Set<Long> getLdapVoIdsWhereUserIsMember(LdapAdapterImpl ldapAdapter, Long userId, String memberOfIdentifier) {
        if (!StringUtils.hasText(memberOfIdentifier)){
            return new HashSet<>();
        }

        PerunAttributeValue attributeValue = ldapAdapter.getAttributeValue(USER, userId, memberOfIdentifier);
        if (attributeValue != null && attributeValue.valueAsList() != null) {
            Set<Long> voIds = new HashSet<>();
            for (String memberOfValue: attributeValue.valueAsList()) {
                String[] parts = memberOfValue.split(",",3);
                String voId = parts[1];
                voId = voId.replace(PERUN_VO_ID + '=',"");
                voIds.add(Long.parseLong(voId));
            }
            return voIds;
        }

        return new HashSet<>();
    }
}
