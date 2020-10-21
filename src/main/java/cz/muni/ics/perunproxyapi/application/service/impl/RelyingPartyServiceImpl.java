package cz.muni.ics.perunproxyapi.application.service.impl;

import cz.muni.ics.perunproxyapi.application.service.RelyingPartyService;
import cz.muni.ics.perunproxyapi.application.service.ServiceUtils;
import cz.muni.ics.perunproxyapi.persistence.adapters.DataAdapter;
import cz.muni.ics.perunproxyapi.persistence.enums.MemberStatus;
import cz.muni.ics.perunproxyapi.persistence.exceptions.PerunConnectionException;
import cz.muni.ics.perunproxyapi.persistence.exceptions.PerunUnknownException;
import cz.muni.ics.perunproxyapi.persistence.models.Facility;
import cz.muni.ics.perunproxyapi.persistence.models.Group;
import cz.muni.ics.perunproxyapi.persistence.models.Member;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public boolean isTestSp(@NonNull DataAdapter adapter, @NonNull Long facilityId, String isTestSpIdentifier)
            throws PerunUnknownException, PerunConnectionException {
        return adapter.isTestSp(facilityId, isTestSpIdentifier);
    }

    @Override
    public boolean hasAccessToService(@NonNull DataAdapter adapter, @NonNull Long facilityId, @NonNull List<Member> members,
                                      @NonNull List<Long> voIds, String checkGroupMembershipAttrIdentifier, String isTestSpIdentifier)
            throws PerunUnknownException, PerunConnectionException {

        boolean isValidMemberOfVo = members.stream()
                .filter(member -> member.getStatus().equals(MemberStatus.VALID))
                .anyMatch(member -> voIds.contains(member.getVoId()));

        if (!isValidMemberOfVo) {
            return false;
        }

        boolean checkGroupMembership = adapter.checkGroupMembership(facilityId, checkGroupMembershipAttrIdentifier);

        if (!checkGroupMembership) {
            return true;
        }

        List<Group> facilityGroups = adapter.getAllowedGroups(facilityId);

        List<Group> activeMemberGroups = new ArrayList<>();
        for (Member member : members) {
            List<Group> mg = adapter.getGroupsWhereMemberIsActive(member.getId());
            activeMemberGroups.addAll(mg);
        }

        Set<Group> intersection = facilityGroups.stream()
                .distinct()
                .filter(activeMemberGroups::contains)
                .collect(Collectors.toSet());

        return !intersection.isEmpty();
    }

}
