package uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.validation;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.DomainCreationRequest.aDomainCreationRequest;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.UserCreationRequest.aUserCreationRequest;
import static uk.gov.hmcts.reform.professionalapi.utils.OrganisationFixtures.someMinimalOrganisationRequest;

import org.junit.Test;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;

public class EmailAddressValidatorTest {

    private final EmailAddressValidator emailAddressValidator = new EmailAddressValidator();

    @Test
    public void throws_when_email_doesnt_have_a_matching_domain() {

        OrganisationCreationRequest organisationCreationRequest = someMinimalOrganisationRequest()
                .superUser(aUserCreationRequest()
                        .email("user@somedomain.com").build())
                .domains(asList(aDomainCreationRequest()
                        .domain("some-other-domain-not-matching-email-address.com")
                        .build()))
                .build();

        assertThatThrownBy(() -> emailAddressValidator.validate(organisationCreationRequest))
                .isExactlyInstanceOf(InvalidRequest.class)
                .hasMessage("The email address does not match any of the provided domains");
    }

    @Test
    public void throws_when_email_doesnt_have_a_matching_domain_given_multiple_domains() {

        OrganisationCreationRequest organisationCreationRequest = someMinimalOrganisationRequest()
                .superUser(aUserCreationRequest()
                        .email("user@somedomain.com").build())
                .domains(asList(aDomainCreationRequest()
                                .domain("some-other-domain-not-matching-email-address.com")
                                .build(),
                        aDomainCreationRequest()
                                .domain("some-other-domain-also-not-matching-email-address.com")
                                .build()))
                .build();

        assertThatThrownBy(() -> emailAddressValidator.validate(organisationCreationRequest))
                .isExactlyInstanceOf(InvalidRequest.class)
                .hasMessage("The email address does not match any of the provided domains");
    }

    @Test
    public void validates_when_email_has_a_matching_domain_given_multiple_domains() {

        OrganisationCreationRequest organisationCreationRequest = someMinimalOrganisationRequest()
                .superUser(aUserCreationRequest()
                        .email("user@somedomain.com").build())
                .domains(asList(aDomainCreationRequest()
                                .domain("some-other-domain-not-matching-email-address.com")
                                .build(),
                        aDomainCreationRequest()
                                .domain("somedomain.com")
                                .build()))
                .build();

        assertThatCode(() -> emailAddressValidator.validate(organisationCreationRequest))
                .doesNotThrowAnyException();
    }

    @Test
    public void validates_when_email_has_a_matching_domain_given_a_single_domain() {

        OrganisationCreationRequest organisationCreationRequest = someMinimalOrganisationRequest()
                .superUser(aUserCreationRequest()
                        .email("user@somedomain.com").build())
                .domains(asList(aDomainCreationRequest()
                                .domain("somedomain.com")
                                .build()))
                .build();

        assertThatCode(() -> emailAddressValidator.validate(organisationCreationRequest))
                .doesNotThrowAnyException();
    }
}