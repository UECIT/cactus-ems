package uk.nhs.ctp.auditFinder.finder;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import uk.nhs.ctp.audit.model.AuditSession;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("dev")
public class LocalAuditFinder implements AuditFinder {

    private final RestTemplate auditRestTemplate;

    @Value("${es.audit}")
    private String auditFinderEndpoint;

    @Override
    public List<AuditSession> findAll(Long caseId) {
        if (StringUtils.isEmpty(auditFinderEndpoint)) {
            log.info("No audit finder endpoint configured");
            return Collections.emptyList();
        }

        try {
            var audits = auditRestTemplate.getForObject(
                    auditFinderEndpoint + "/caseId/{caseId}",
                    AuditSession[].class,
                    caseId);

            return List.of(audits);
        } catch (ResourceAccessException e) {
            log.info("Audit finder configured but cannot connect");
            return Collections.emptyList();
        }
    }
}