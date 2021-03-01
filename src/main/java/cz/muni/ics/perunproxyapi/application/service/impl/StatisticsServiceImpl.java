package cz.muni.ics.perunproxyapi.application.service.impl;

import cz.muni.ics.perunproxyapi.application.service.StatisticsService;
import cz.muni.ics.perunproxyapi.persistence.exceptions.DBOperationException;
import cz.muni.ics.perunproxyapi.persistence.exceptions.EntityNotFoundException;
import cz.muni.ics.perunproxyapi.persistence.managers.StatisticsManager;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsManager statisticsManager;

    @Autowired
    public StatisticsServiceImpl(@NonNull StatisticsManager statisticsManager) {
        this.statisticsManager = statisticsManager;
    }

    @Override
    public boolean logStatistics(@NonNull String userId,
                                 @NonNull String rpIdentifier,
                                 @NonNull String rpName,
                                 @NonNull String idpIdentifier,
                                 @NonNull String idpName)
    {
        try {
            statisticsManager.insertIdp(idpIdentifier, idpName);
            statisticsManager.insertRp(rpIdentifier, rpName);
            int idpId = statisticsManager.extractIdpId(idpIdentifier);
            int rpId = statisticsManager.extractRpId(rpIdentifier);
            statisticsManager.insertLogin(rpId, idpId, userId);
        } catch (EntityNotFoundException | DBOperationException e) {
            return false;
        }
        return true;
    }

}
