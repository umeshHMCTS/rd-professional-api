package uk.gov.hmcts.reform.professionalapi.utils;

import static java.util.Arrays.asList;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.DomainCreationRequest.aDomainCreationRequest;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest.OrganisationCreationRequestBuilder;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest.anOrganisationCreationRequest;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.UserCreationRequest.aUserCreationRequest;

public class OrganisationFixtures {

    private OrganisationFixtures() {
    }

    public static OrganisationCreationRequestBuilder someMinimalOrganisationRequest() {

        return anOrganisationCreationRequest()
                .name("some-org-name")
                .domains(asList(aDomainCreationRequest()
                        .domain("somewhere.com")
                        .build()))
                .superUser(aUserCreationRequest()
                        .firstName("fname")
                        .lastName("lname")
                        .email("email-address@somewhere.com")
                        .build());

    }

}
