package cz.muni.ics.perunproxyapi.application.facade.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.ics.perunproxyapi.application.facade.FacadeUtils;
import cz.muni.ics.perunproxyapi.application.facade.ProxyuserFacade;
import cz.muni.ics.perunproxyapi.application.facade.configuration.FacadeConfiguration;
import cz.muni.ics.perunproxyapi.application.service.ProxyUserService;
import cz.muni.ics.perunproxyapi.ga4gh.service.Ga4ghService;
import cz.muni.ics.perunproxyapi.persistence.adapters.DataAdapter;
import cz.muni.ics.perunproxyapi.persistence.adapters.FullAdapter;
import cz.muni.ics.perunproxyapi.persistence.adapters.impl.AdaptersContainer;
import cz.muni.ics.perunproxyapi.persistence.exceptions.EntityNotFoundException;
import cz.muni.ics.perunproxyapi.persistence.exceptions.InvalidRequestParameterException;
import cz.muni.ics.perunproxyapi.persistence.exceptions.PerunConnectionException;
import cz.muni.ics.perunproxyapi.persistence.exceptions.PerunUnknownException;
import cz.muni.ics.perunproxyapi.persistence.models.UpdateAttributeMappingEntry;
import cz.muni.ics.perunproxyapi.persistence.models.User;
import cz.muni.ics.perunproxyapi.presentation.DTOModels.UserDTO;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.management.InvalidAttributeValueException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProxyuserFacadeImpl implements ProxyuserFacade {

    public static final String FIND_BY_EXT_LOGINS = "find_by_ext_logins";
    public static final String FIND_BY_IDENTIFIERS = "find_by_identifiers";
    public static final String GET_USER_BY_LOGIN = "get_user_by_login";
    public static final String FIND_BY_PERUN_USER_ID = "find_by_perun_user_id";
    public static final String GET_ALL_ENTITLEMENTS = "get_all_entitlements";
    public static final String UPDATE_USER_IDENTITY_ATTRIBUTES = "update_user_identity_attributes";

    public static final String PREFIX = "prefix";
    public static final String AUTHORITY = "authority";
    public static final String FORWARDED_ENTITLEMENTS = "forwarded_entitlements";
    public static final String DEFAULT_FIELDS = "default_fields";
    public static final String ATTR_MAPPER = "attr_mapper";

    public static final String CREATE_PROXY_USER = "create_proxy_user";
    public static final String VO_ID = "vo_id";
    public static final String REQUIRED_ATTRIBUTES = "required_attributes";
    public static final String LOGIN_ATTRIBUTES = "login_attributes";
    public static final String CANDIDATE_MAPPER = "candidate_mapper";

    private final Map<String, JsonNode> methodConfigurations;
    private final AdaptersContainer adaptersContainer;
    private final ProxyUserService proxyUserService;
    private final Ga4ghService ga4ghService;

    @Autowired
    public ProxyuserFacadeImpl(@NonNull ProxyUserService proxyUserService,
                               @NonNull AdaptersContainer adaptersContainer,
                               @NonNull FacadeConfiguration facadeConfiguration,
                               @NonNull Ga4ghService ga4ghService)
    {
        this.proxyUserService = proxyUserService;
        this.adaptersContainer = adaptersContainer;
        this.methodConfigurations = facadeConfiguration.getProxyUserAdapterMethodConfigurations();
        this.ga4ghService = ga4ghService;
    }

    @Override
    public UserDTO findByExtLogins(@NonNull String idpIdentifier, @NonNull List<String> userIdentifiers,
                                   List<String> fields)
            throws PerunUnknownException, PerunConnectionException, EntityNotFoundException
    {
        if (userIdentifiers.isEmpty()) {
            throw new IllegalArgumentException("List of identifiers cannot be empty");
        }

        JsonNode options = FacadeUtils.getOptions(FIND_BY_EXT_LOGINS, methodConfigurations);
        DataAdapter adapter = FacadeUtils.getAdapter(adaptersContainer, options);
        List<String> fieldsToFetch = this.getFields(fields, FIND_BY_EXT_LOGINS, options);

        User user = proxyUserService.findByExtLogins(adapter, idpIdentifier,
                userIdentifiers, fieldsToFetch);
        if (user == null) {
            throw new EntityNotFoundException("No user has been found for given identifiers");
        }

        return FacadeUtils.mapUserToUserDTO(user);
    }

    @Override
    public UserDTO findByIdentifiers(@NonNull String idpIdentifier, @NonNull List<String> identifiers, List<String> fields)
            throws EntityNotFoundException
    {
        JsonNode options = FacadeUtils.getOptions(FIND_BY_IDENTIFIERS, methodConfigurations);
        //TODO: currently works only with LDAP
        //DataAdapter adapter = FacadeUtils.getAdapter(adaptersContainer, options);
        DataAdapter adapter = adaptersContainer.getLdapAdapter();

        List<String> fieldsToFetch = this.getFields(fields, FIND_BY_IDENTIFIERS, options);
        User user = proxyUserService.findByIdentifiers(adapter, idpIdentifier, identifiers,
                fieldsToFetch);
        if (user == null) {
            throw new EntityNotFoundException("No user has been found for given identifiers");
        }
        return FacadeUtils.mapUserToUserDTO(user);
    }

    @Override
    public UserDTO getUserByLogin(@NonNull String login, List<String> fields)
            throws PerunUnknownException, PerunConnectionException, EntityNotFoundException
    {
        JsonNode options = FacadeUtils.getOptions(GET_USER_BY_LOGIN, methodConfigurations);
        DataAdapter adapter = FacadeUtils.getAdapter(adaptersContainer, options);
        List<String> fieldsToFetch = this.getFields(fields, GET_USER_BY_LOGIN, options);

        User user = proxyUserService.getUserWithAttributesByLogin(adapter, login, fieldsToFetch);
        if (user == null) {
            throw new EntityNotFoundException("No user has been found for given login");
        }

        return FacadeUtils.mapUserToUserDTO(user);
    }

    @Override
    public UserDTO findByPerunUserId(Long userId, List<String> fields)
            throws PerunUnknownException, PerunConnectionException, EntityNotFoundException
    {
        JsonNode options = FacadeUtils.getOptions(FIND_BY_PERUN_USER_ID, methodConfigurations);
        DataAdapter adapter = FacadeUtils.getAdapter(adaptersContainer, options);
        List<String> fieldsToFetch = this.getFields(fields, FIND_BY_PERUN_USER_ID, options);

        User user = proxyUserService.findByPerunUserIdWithAttributes(adapter, userId, fieldsToFetch);
        if (user == null) {
            throw new EntityNotFoundException("No user has been found for given user ID");
        }

        return FacadeUtils.mapUserToUserDTO(user);
    }

    @Override
    public List<String> getAllEntitlements(String login)
            throws PerunUnknownException, PerunConnectionException, EntityNotFoundException
    {
        JsonNode options = FacadeUtils.getOptions(GET_ALL_ENTITLEMENTS, methodConfigurations);
        DataAdapter adapter = FacadeUtils.getAdapter(adaptersContainer, options);

        String prefix = FacadeUtils.getRequiredStringOption(PREFIX, GET_ALL_ENTITLEMENTS, options);
        String authority = FacadeUtils.getRequiredStringOption(AUTHORITY, GET_ALL_ENTITLEMENTS, options);
        String forwardedEntitlementsAttrIdentifier = FacadeUtils.getStringOption(FORWARDED_ENTITLEMENTS, options);

        User user = proxyUserService.getUserByLogin(adapter, login);
        if (user == null) {
            throw new EntityNotFoundException("User for given login could not be found");
        }

        List<String> entitlements = proxyUserService.getAllEntitlements(adapter, user.getPerunId(), prefix, authority,
                forwardedEntitlementsAttrIdentifier);
        if (entitlements != null) {
            Collections.sort(entitlements);
        }
        return entitlements;
    }

    @Override
    public boolean updateUserIdentityAttributes(@NonNull String login, @NonNull String identityId,
                                                @NonNull Map<String, JsonNode> requestAttributes)
            throws PerunUnknownException, PerunConnectionException
    {
        JsonNode options = FacadeUtils.getOptions(UPDATE_USER_IDENTITY_ATTRIBUTES, methodConfigurations);
        if (!options.hasNonNull(ATTR_MAPPER)) {
            log.error("Required option {} has not been found by the updateUserIdentityAttributes method. " +
                    "Check your configuration.", ATTR_MAPPER);
            throw new IllegalArgumentException("Required option has not been found");
        }
        JsonNode attributeMapper = options.get(ATTR_MAPPER);
        Map<String, UpdateAttributeMappingEntry> mapper = new ObjectMapper()
                .convertValue(attributeMapper, new TypeReference<>() {});
        FullAdapter adapter = adaptersContainer.getRpcAdapter();

        final Map<String, String> externalToInternal = new HashMap<>();
        List<String> searchAttributes = mapper.entrySet()
                .stream()
                .peek(entry -> {
                    List<String> externalNames = entry.getValue().getExternalNames();
                    String internalName = entry.getKey();
                    for (String externalName: externalNames) {
                        externalToInternal.put(externalName, internalName);
                    }
                })
                .filter(entry -> entry.getValue().isUseForSearch())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return proxyUserService.updateUserIdentityAttributes(login, identityId, adapter, requestAttributes,
                mapper, externalToInternal, searchAttributes);
    }

    @Override
    public JsonNode ga4ghById(@NonNull Long userId)
            throws PerunUnknownException, PerunConnectionException, EntityNotFoundException
    {
        return ga4gh(userId, null);
    }

    @Override
    public JsonNode ga4ghByLogin(@NonNull String login)
            throws PerunUnknownException, PerunConnectionException, EntityNotFoundException
    {
        return ga4gh(null, login);
    }

    @Override
    public boolean create(@NonNull String extSourceIdentifier, @NonNull Map<String, JsonNode> requestAttributes)
            throws IOException, PerunUnknownException, PerunConnectionException, InvalidRequestParameterException
    {
        JsonNode options = FacadeUtils.getOptions(CREATE_PROXY_USER, methodConfigurations);
        Long voId = FacadeUtils.getRequiredLongOption(VO_ID, CREATE_PROXY_USER, options);

        FullAdapter adapter = adaptersContainer.getRpcAdapter();
        List<String> requiredAttrs = FacadeUtils.getRequiredStringListOption(
                REQUIRED_ATTRIBUTES, CREATE_PROXY_USER, options);
        List<String> loginAttrs = FacadeUtils.getRequiredStringListOption(
                LOGIN_ATTRIBUTES, CREATE_PROXY_USER, options);
        Map<String, String> perunAttrsMapper = FacadeUtils.externalToInternalAttributeMapper(
                ATTR_MAPPER, CREATE_PROXY_USER, options);
        BidiMap<String, String> candidateAttrsMapper = new DualHashBidiMap<>(
                FacadeUtils.externalToInternalAttributeMapper(CANDIDATE_MAPPER, CREATE_PROXY_USER, options));
        if (!requestAttributes.keySet().containsAll(requiredAttrs)) {
            throw new InvalidRequestParameterException("Not all required attributes have been provided.");
        }

        String login = null;
        for (String loginAttr: loginAttrs) {
            if (requestAttributes.containsKey(loginAttr)
                    && StringUtils.hasText(requestAttributes.get(loginAttr).textValue())) {
                login = requestAttributes.get(loginAttr).textValue();
                break;
            }
        }

        if (login == null) {
            throw new InvalidRequestParameterException("No attributes for login have been sent in a request body.");
        }

        return proxyUserService.create(adapter, extSourceIdentifier, voId, login, requestAttributes,
                perunAttrsMapper, candidateAttrsMapper);
    }

    private JsonNode ga4gh(Long perunUserId, String userLogin) throws PerunConnectionException, PerunUnknownException,
            EntityNotFoundException
    {
        FullAdapter adapter = FacadeUtils.getFullAdapter(adaptersContainer);
        if (perunUserId == null) {
            User user = proxyUserService.getUserByLogin(adapter, userLogin);
            if (user == null) {
                throw new EntityNotFoundException("No user found for given login");
            }
            perunUserId = user.getPerunId();
        }
        return ga4ghService.getPassportsAndVisas(adapter, perunUserId);
    }

    private List<String> getFields(List<String> fields, String method, JsonNode options) {
        return (fields != null) ? fields : this.getDefaultFields(method, options);
    }

    private List<String> getDefaultFields(String method, JsonNode options) {
        List<String> fields = new ArrayList<>();
        if (!options.hasNonNull(DEFAULT_FIELDS)) {
                log.error("Required option {} has not been found by method {}. Check your configuration.",
                        method, DEFAULT_FIELDS);
                throw new IllegalArgumentException("Required option has not been found");
        }

        for (JsonNode subNode : options.get(DEFAULT_FIELDS)) {
            if (!subNode.isNull()) {
                String attr = subNode.asText();
                if (StringUtils.hasText(attr)) {
                    fields.add(attr);
                }
            }
        }

        return fields;
    }

}
