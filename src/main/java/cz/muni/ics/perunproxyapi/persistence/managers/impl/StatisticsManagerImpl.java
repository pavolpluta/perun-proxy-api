package cz.muni.ics.perunproxyapi.persistence.managers.impl;

import cz.muni.ics.perunproxyapi.persistence.connectors.properties.StatisticsDbProperties;
import cz.muni.ics.perunproxyapi.persistence.exceptions.DBOperationException;
import cz.muni.ics.perunproxyapi.persistence.exceptions.EntityNotFoundException;
import cz.muni.ics.perunproxyapi.persistence.managers.StatisticsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class StatisticsManagerImpl implements StatisticsManager {

    private static final String PARAM_DATE = "date";
    private static final String PARAM_IDP_ID = "idpId";
    private static final String PARAM_RP_ID = "rpId";
    private static final String PARAM_USER = "user";
    private static final String PARAM_IDENTIFIER = "identifier";
    private static final String PARAM_NAME = "name";

    private final StatisticsDbProperties dbProperties;
    private final NamedParameterJdbcTemplate template;

    @Autowired
    public StatisticsManagerImpl(StatisticsDbProperties dbProperties,
                                 @Qualifier("statisticsJdbcTemplate") NamedParameterJdbcTemplate template)
    {
        this.dbProperties = dbProperties;
        this.template = template;
    }

    @Override
    public void insertLogin(int rpId, int idpId, String login) throws DBOperationException {
        String queryBase = "INSERT INTO %s(day, idpId, spId, user, logins)" +
                " VALUES(:%s, :%s, :%s, :%s, '1') " +
                " ON DUPLICATE KEY UPDATE logins = logins + 1";
        String query = String.format(queryBase, dbProperties.getStatisticsTable(),
                PARAM_DATE, PARAM_IDP_ID, PARAM_RP_ID, PARAM_USER);

        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_DATE, LocalDate.now());
        params.put(PARAM_IDP_ID, idpId);
        params.put(PARAM_RP_ID, idpId);
        params.put(PARAM_USER, login);

        try {
            template.update(query, new MapSqlParameterSource(params));
        } catch (Exception e) {
            log.error("Caught exception when inserting login");
            log.debug("", e);
            throw new DBOperationException();
        }
    }

    @Override
    public int extractRpId(String rpIdentifier) throws EntityNotFoundException, DBOperationException {
        return extractId(rpIdentifier, dbProperties.getRpMapTable(), "spId", "RP");
    }

    @Override
    public int extractIdpId(String idpIdentifier) throws EntityNotFoundException, DBOperationException {
        return extractId(idpIdentifier, dbProperties.getIdpMapTable(), "idpId", "IDP");
    }

    @Override
    public void insertRp(String rpIdentifier, String rpName) throws DBOperationException {
        insertIntoMapTable(rpIdentifier, rpName, dbProperties.getRpMapTable(), "RP");
    }

    @Override
    public void insertIdp(String idpIdentifier, String idpName) throws DBOperationException {
        insertIntoMapTable(idpIdentifier, idpName, dbProperties.getIdpMapTable(), "IDP");
    }

    private int extractId(String identifier, String table, String column, String type)
            throws EntityNotFoundException, DBOperationException {
        String queryBase = "SELECT %s FROM %s WHERE identifier = :%s";
        String query = String.format(queryBase, column, table, PARAM_IDENTIFIER);
        Map<String, Object> params = Collections.singletonMap(PARAM_IDENTIFIER, identifier);
        try {
            Integer result = template.queryForObject(query, new MapSqlParameterSource(params), Integer.class);
            if (result == null) {
                throw new EntityNotFoundException("No entry found for identifier");
            }
            return result;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Caught exception when fetching {} ID for identifier: {}", type, identifier);
            log.debug("", e);
            throw new DBOperationException();
        }
    }

    private void insertIntoMapTable(String identifier, String rpName, String table, String type)
            throws DBOperationException
    {
        String queryBase = "INSERT INTO %s(identifier, name)" +
                " VALUES (:%s, :%s) ON DUPLICATE KEY UPDATE name = :%s)";
        String query = String.format(queryBase, table, PARAM_IDENTIFIER, PARAM_NAME, PARAM_NAME);

        Map<String, Object> params = new HashMap<>();
        params.put(PARAM_IDENTIFIER, identifier);
        params.put(PARAM_NAME, rpName);

        try {
            template.update(query, new MapSqlParameterSource(params));
        } catch (Exception e) {
            log.error("Caught exception when inserting {}", type);
            log.debug("", e);
            throw new DBOperationException();
        }
    }

}
