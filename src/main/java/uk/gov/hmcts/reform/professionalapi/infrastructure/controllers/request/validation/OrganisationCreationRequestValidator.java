package uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.validation;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;

@Service
public class OrganisationCreationRequestValidator {

    private final List<OrganisationRequestValidator> validators;

    public OrganisationCreationRequestValidator(List<OrganisationRequestValidator> validators) {
        this.validators = validators;
    }

    public void validate(OrganisationCreationRequest organisationCreationRequest) {
        validators.forEach(v -> v.validate(organisationCreationRequest));
    }

}
