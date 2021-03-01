package cz.muni.ics.perunproxyapi.presentation.rest.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cz.muni.ics.perunproxyapi.presentation.rest.config.WebConstants.NO_AUTH_PATH;
import static cz.muni.ics.perunproxyapi.presentation.rest.config.WebConstants.RELYING_PARTY;


/**
 * Controller containing methods related to proxy user. No auth is required.
 * methods path: /CONTEXT_PATH/non/relying-party/**
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
@RestController
@RequestMapping(value = NO_AUTH_PATH + RELYING_PARTY)
@Slf4j
public class RelyingPartyUnprotectedController {

}
