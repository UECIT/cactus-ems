package uk.nhs.ctp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.search.AuditSearchRequest;
import uk.nhs.ctp.service.search.AuditSearchResultDTO;

@Service
@RequiredArgsConstructor
public class AuditSearchService {

  private final CaseRepository caseRepository;
  private final TokenAuthenticationService authenticationService;

  public Page<AuditSearchResultDTO> search(AuditSearchRequest request) {
    return caseRepository.search(
        request.getFrom(), request.getTo(),
        request.isIncludeClosed(), request.isIncludeIncomplete(),
        authenticationService.requireSupplierId(),
        request);
  }
}
