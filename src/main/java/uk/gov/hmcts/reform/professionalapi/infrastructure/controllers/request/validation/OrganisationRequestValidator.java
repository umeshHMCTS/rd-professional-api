package uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.validation;

import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;

public interface OrganisationRequestValidator {

    void validate(OrganisationCreationRequest organisationCreationRequest);
}
