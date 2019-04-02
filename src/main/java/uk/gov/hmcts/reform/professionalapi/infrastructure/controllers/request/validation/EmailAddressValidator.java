package uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.validation;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.DomainCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;

@Service
public class EmailAddressValidator implements OrganisationRequestValidator {

    @Override
    public void validate(OrganisationCreationRequest organisationCreationRequest) {

        List<DomainCreationRequest> domainsRequests = organisationCreationRequest.getDomains();

        String superUserEmailAddressDomain = organisationCreationRequest
                .getSuperUser()
                .getEmail()
                .split("@")[1];

        domainsRequests.stream()
                .filter(domain -> domain.getDomain().equals(superUserEmailAddressDomain))
                .findFirst()
                .orElseThrow(() -> new InvalidRequest("The email address does not match any of the provided domains"));
    }
}
