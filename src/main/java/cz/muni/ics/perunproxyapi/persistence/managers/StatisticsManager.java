package cz.muni.ics.perunproxyapi.persistence.managers;

import cz.muni.ics.perunproxyapi.persistence.exceptions.DBOperationException;
import cz.muni.ics.perunproxyapi.persistence.exceptions.EntityNotFoundException;

public interface StatisticsManager {

    /**
     * Insert entry for login
     * @param rpId ID of RP in DB
     * @param idpId ID of IdP in DB
     * @param login users login
     * @throws DBOperationException Thrown when operation in DB has failed.
     */
    void insertLogin(int rpId, int idpId, String login) throws DBOperationException;

    /**
     * Fetch RP_ID for RP specified by identifier (client_id or entity_id)
     * @param rpIdentifier Identifier of the RP
     * @return extracted ID from DB
     * @throws EntityNotFoundException Thrown when no entity exists for specified identifier.
     * @throws DBOperationException Thrown when operation in DB has failed.
     */
    int extractRpId(String rpIdentifier) throws EntityNotFoundException, DBOperationException;

    /**
     * Fetch IDP_ID for IdP specified by identifier (entity_id of the IdP)
     * @param idpEntityId Identifier of the IdP
     * @return extracted ID from DB
     * @throws EntityNotFoundException Thrown when no entity exists for specified identifier.
     * @throws DBOperationException Thrown when operation in DB has failed.
     */
    int extractIdpId(String idpEntityId) throws EntityNotFoundException, DBOperationException;

    /**
     * Insert or update mapping of the RP identifier to RP name
     * @param rpIdentifier identifier of the RP
     * @param rpName name of the RP
     * @throws DBOperationException Thrown when operation in DB has failed.
     */
    void insertRp(String rpIdentifier, String rpName) throws DBOperationException;

    /**
     * Insert or update mapping of the IdP identifier to IdP name
     * @param idpIdentifier identifier of the IdP
     * @param idpName name of the IdP
     * @throws DBOperationException Thrown when operation in DB has failed.
     */
    void insertIdp(String idpIdentifier, String idpName) throws DBOperationException;

}
