package cz.muni.ics.perunproxyapi.application.service;

import lombok.NonNull;

/**
 * Service layer for STATISTICS related things. Purpose of this class is to execute correct methods on the given adapter.
 *
 * @author Dominik Frantisek Bucik <bucik@ics.muni.cz>
 */
public interface StatisticsService {

    /**
     * Log statistics about login into corresponding table
     * @param userId User ID
     * @param rpIdentifier RP identifier
     * @param rpName Name of the Rp
     * @param idpEntityId ID of the IDP entity
     * @param idpName Name of the IDP
     * @return TRUE if data were inserted into the table, otherwise FALSE.
     */
    boolean logStatistics(@NonNull String userId,
                          @NonNull String rpIdentifier,
                          @NonNull String rpName,
                          @NonNull String idpEntityId,
                          @NonNull String idpName);

}
