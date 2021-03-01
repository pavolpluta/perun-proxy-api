package cz.muni.ics.perunproxyapi.application.facade;

import lombok.NonNull;

/**
 * Facade for STATISTICS related things. Purpose of this class is to execute correct lower-level methods
 * to achieve the desired results.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public interface StatisticsFacade {

    /**
     * Log statistics about login into corresponding table
     * @param login Login of the user
     * @param rpIdentifier RP identifier
     * @param rpName Name of the RP
     * @param idpIdentifier IdP identifier
     * @param idpName Name of IDP
     * @return TRUE if data were inserted into the table, FALSE otherwise.
     */
    boolean logStatistics(@NonNull String login, @NonNull String rpIdentifier, @NonNull String rpName,
                          @NonNull String idpIdentifier, @NonNull String idpName);

}
