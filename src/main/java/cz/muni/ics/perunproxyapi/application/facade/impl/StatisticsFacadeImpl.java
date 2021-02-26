package cz.muni.ics.perunproxyapi.application.facade.impl;

import com.fasterxml.jackson.databind.JsonNode;
import cz.muni.ics.perunproxyapi.application.facade.StatisticsFacade;
import cz.muni.ics.perunproxyapi.application.facade.configuration.FacadeConfiguration;
import cz.muni.ics.perunproxyapi.application.service.StatisticsService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class StatisticsFacadeImpl implements StatisticsFacade {

    private final Map<String, JsonNode> methodConfigurations;
    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsFacadeImpl(@NonNull FacadeConfiguration facadeConfiguration,
                                @NonNull StatisticsService statisticsService)
    {
        this.methodConfigurations = facadeConfiguration.getRelyingPartyAdapterMethodConfigurations();
        this.statisticsService = statisticsService;
    }

    @Override
    public boolean logStatistics(@NonNull String login,
                                 @NonNull String rpIdentifier,
                                 @NonNull String rpName,
                                 @NonNull String idpIdentifier,
                                 @NonNull String idpName)
    {
        return statisticsService.logStatistics(login, rpIdentifier, rpName, idpIdentifier, idpName);
    }

}
