package uk.nhs.ctp.caseSearch;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.repos.CaseRepository;

@Service
@RequiredArgsConstructor
public class CaseSearchService {

  private final CaseRepository caseRepository;
  private final TokenAuthenticationService authenticationService;

  public Page<CaseSearchResultDTO> search(CaseSearchRequest request) {
    return caseRepository.search(
        request.getFrom(), request.getTo(),
        request.isIncludeClosed(), request.isIncludeIncomplete(),
        authenticationService.requireSupplierId(),
        request);
  }
}
