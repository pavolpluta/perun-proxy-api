package cz.muni.ics.perunproxyapi.presentation.rest.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import cz.muni.ics.perunproxyapi.application.facade.StatisticsFacade;
import cz.muni.ics.perunproxyapi.persistence.exceptions.InvalidRequestParameterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static cz.muni.ics.perunproxyapi.presentation.rest.config.WebConstants.AUTH_PATH;
import static cz.muni.ics.perunproxyapi.presentation.rest.config.WebConstants.IDP_IDENTIFIER;
import static cz.muni.ics.perunproxyapi.presentation.rest.config.WebConstants.IDP_NAME;
import static cz.muni.ics.perunproxyapi.presentation.rest.config.WebConstants.LOGIN;
import static cz.muni.ics.perunproxyapi.presentation.rest.config.WebConstants.RP_IDENTIFIER;
import static cz.muni.ics.perunproxyapi.presentation.rest.config.WebConstants.RP_NAME;
import static cz.muni.ics.perunproxyapi.presentation.rest.config.WebConstants.STATISTICS;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = AUTH_PATH + STATISTICS)
@Slf4j
public class StatisticsProtectedController {

    private final StatisticsFacade facade;

    @Autowired
    public StatisticsProtectedController(StatisticsFacade facade) {
        this.facade = facade;
    }

    /**
     * <pre>
     * Log statistics about login into corresponding tables
     *
     * EXAMPLE CURL:
     *  curl --request PUT \
     *   --url http://localhost:8080/proxyapi/auth/statistics \
     *   --header 'Authorization: Basic auth' \
     *   --header 'Content-Type: application/json' \
     *   --data '{
     * 	    "login": "test_user_login",
     * 	    "rp-identifier": "test_rpIdentifier",
     * 	    "rp-name": "test_rpName",
     * 	    "idp-identifier": "test_idpEntityId",
     * 	    "idp-name": "test_IdpName"
     *    }'
     * </pre>
     * @param body json body corresponding of required attributes:
     *             - login: identifier of the user
     *             - rp-identifier: identifier of the Relying Party in base64url safe format
     *             - rp-name: name of the relying party the user has accessed
     *             - idp-identifier: identifier of the Identity Provider in base64url safe format
     *             - idp-name: name of the Identity Provider the user has used
     * @return HTTP Status 200 if data was successfully logged into statistics table, otherwise 404.
     * @throws InvalidRequestParameterException Thrown when passed request parameters do not meet criteria.
     */
    @ResponseBody
    @PutMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public boolean logStatistics(@RequestBody JsonNode body) throws InvalidRequestParameterException {
        if (body == null) {
            throw new InvalidRequestParameterException("Request body is empty.");
        }

        String login = ControllerUtils.extractRequiredString(body, LOGIN);
        String rpIdentifier = ControllerUtils.extractRequiredString(body, RP_IDENTIFIER);
        String rpIdentifierDecoded = ControllerUtils.decodeUrlSafeBase64(rpIdentifier);
        String rpName = ControllerUtils.extractRequiredString(body, RP_NAME);
        String idpIdentifier = ControllerUtils.extractRequiredString(body, IDP_IDENTIFIER);
        String idpIdentifierDecoded = ControllerUtils.decodeUrlSafeBase64(idpIdentifier);
        String idpName = ControllerUtils.extractRequiredString(body, IDP_NAME);

        if (!StringUtils.hasText(rpIdentifierDecoded)) {
            throw new InvalidRequestParameterException("Could not decode RP identifier");
        } else if (!StringUtils.hasText(idpIdentifierDecoded)) {
            throw new InvalidRequestParameterException("Could not decode IDP identifier");
        }

        return facade.logStatistics(login, rpIdentifierDecoded, rpName, idpIdentifierDecoded, idpName);
    }

}
