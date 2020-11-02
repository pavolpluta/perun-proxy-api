package cz.muni.ics.perunproxyapi.application.service.impl;

import cz.muni.ics.perunproxyapi.application.service.RelyingPartyService;
import cz.muni.ics.perunproxyapi.application.service.ServiceUtils;
import cz.muni.ics.perunproxyapi.persistence.adapters.DataAdapter;
import cz.muni.ics.perunproxyapi.persistence.exceptions.PerunConnectionException;
import cz.muni.ics.perunproxyapi.persistence.exceptions.PerunUnknownException;
import cz.muni.ics.perunproxyapi.persistence.models.Facility;
import cz.muni.ics.perunproxyapi.persistence.models.Group;
import cz.muni.ics.perunproxyapi.persistence.models.PerunAttributeValue;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cz.muni.ics.perunproxyapi.persistence.enums.Entity.FACILITY;

@Component
public class RelyingPartyServiceImpl implements RelyingPartyService {

    @Override
    public List<String> getEntitlements(@NonNull DataAdapter adapter, @NonNull Long facilityId,
                                        @NonNull Long userId, @NonNull String prefix, @NonNull String authority,
                                        String forwardedEntitlementsAttrIdentifier,
                                        String resourceCapabilitiesAttrIdentifier,
                                        String facilityCapabilitiesAttrIdentifier)
            throws PerunUnknownException, PerunConnectionException
    {
        List<String> entitlements = new ArrayList<>(
                adapter.getForwardedEntitlements(userId, forwardedEntitlementsAttrIdentifier)
        );

        List<Group> groups = adapter.getUsersGroupsOnFacility(facilityId, userId);
        if (groups == null || groups.isEmpty()) {
            return entitlements;
        }

        List<String> groupEntitlements = ServiceUtils.wrapGroupEntitlements(groups, prefix, authority);
        entitlements.addAll(groupEntitlements);

        List<String> capabilities = adapter.getCapabilities(facilityId, userId, groups,
                resourceCapabilitiesAttrIdentifier, facilityCapabilitiesAttrIdentifier);
        if (capabilities != null && !capabilities.isEmpty()) {
            entitlements.addAll(capabilities.stream()
                    .map(cap -> ServiceUtils.wrapCapabilityToAARC(cap, prefix, authority))
                    .collect(Collectors.toSet())
            );
        }

        return entitlements;
    }

    @Override
    public Facility getFacilityByIdentifier(@NonNull DataAdapter adapter, @NonNull String rpIdentifier)
            throws PerunUnknownException, PerunConnectionException
    {
        return adapter.getFacilityByRpIdentifier(rpIdentifier);
    }

    @Override
    public boolean isTestSp(@NonNull DataAdapter adapter, @NonNull Long facilityId, @NonNull String isTestSpIdentifier)
            throws PerunUnknownException, PerunConnectionException {
        PerunAttributeValue attributeValue = adapter.getAttributeValue(FACILITY, facilityId, isTestSpIdentifier);
        if (attributeValue != null) {
            return attributeValue.valueAsBoolean();
        }

        return false;
    }

    @Override
    public boolean checkGroupMembership(@NonNull DataAdapter adapter, @NonNull Long facilityId, @NonNull String checkGroupMembershipIdentifier)
            throws PerunUnknownException, PerunConnectionException {
        PerunAttributeValue attributeValue = adapter.getAttributeValue(FACILITY, facilityId, checkGroupMembershipIdentifier);
        if (attributeValue != null) {
            return attributeValue.valueAsBoolean();
        }

        return false;
    }


    @Override
    public boolean hasAccessToService(@NonNull DataAdapter adapter, @NonNull Long facilityId,
                                      @NonNull Long userId, @NonNull List<Long> voIds, @NonNull String checkGroupMembershipAttrIdentifier)
            throws PerunUnknownException, PerunConnectionException {

        if (!adapter.isValidMemberOfAnyProvidedVo(userId, voIds)) {
            return false;
        }

        boolean checkGroupMembership = this.checkGroupMembership(adapter, facilityId, checkGroupMembershipAttrIdentifier);

        if (!checkGroupMembership) {
            return true;
        }

        List<Group> groups = adapter.getFacilityGroupsWhereUserIsValidMember(userId, facilityId);
        return !groups.isEmpty();
    }

}
