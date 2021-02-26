package cz.muni.ics.perunproxyapi.presentation.rest.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants for PATHs mapping.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WebConstants {

    // auth, non-auth, ...
    public static final String NO_AUTH_PATH = "/non";
    public static final String AUTH_PATH = "/auth";

    // controller paths
    public static final String PROXY_USER = "/proxy-user";
    public static final String RELYING_PARTY = "/relying-party";
    public static final String GUI = "/gui";
    public static final String STATISTICS = "/statistics";

    // parameters
    public static final String RP_IDENTIFIER = "rp-identifier";
    public static final String LOGIN = "login";
    public static final String RP_NAME = "rpName";
    public static final String IDP_NAME = "idpName";
    public static final String IDP_IDENTIFIER = "idp-identifier";
    public static final String IDENTIFIERS = "identifiers";
    public static final String FIELDS = "fields";
    public static final String USER_ID = "user-id";
    public static final String ATTRIBUTES = "attributes";
    public static final String EXT_SOURCE_IDENTIFIER = "ext-source-identifier";

}
