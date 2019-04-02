package uk.gov.hmcts.reform.professionalapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.PbaAccountCreationRequest.aPbaPaymentAccount;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.UserCreationRequest.aUserCreationRequest;
import static uk.gov.hmcts.reform.professionalapi.utils.OrganisationFixtures.someMinimalOrganisationRequest;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import uk.gov.hmcts.reform.professionalapi.domain.entities.PaymentAccount;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.util.Service2ServiceEnabledIntegrationTest;

public class CreateOrganisationWithPaymentAccountForSuperuserTest extends Service2ServiceEnabledIntegrationTest {

    @Test
    public void persists_organisation_with_valid_super_user_payment_account() {

        OrganisationCreationRequest organisationCreationRequest = someMinimalOrganisationRequest()
                .pbaAccounts(asList(aPbaPaymentAccount()
                        .pbaNumber("pbaNumber-1")
                        .build()))
                .superUser(aUserCreationRequest()
                        .firstName("some-fname")
                        .lastName("some-lname")
                        .email("someone@somewhere.com")
                        .pbaAccount(aPbaPaymentAccount()
                                .pbaNumber("pbaNumber-1")
                                .build())
                        .build())
                .build();

        Map<String, Object> response =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);

        String orgNameFromResponse = (String) response.get("name");

        List<PaymentAccount> persistedPaymentAccounts = paymentAccountRepository.findAll();

        assertThat(response.get("http_status")).isEqualTo("201");
        assertThat(persistedPaymentAccounts.size()).isEqualTo(1);
        assertThat(persistedPaymentAccounts.get(0).getOrganisation().getName())
                .isEqualTo(orgNameFromResponse);
        assertThat(persistedPaymentAccounts.get(0).getUser().getFirstName())
                .isEqualTo("some-fname");
    }

    @Test
    public void returns_bad_request_when_superuser_pba_number_doesnt_match_one_associated_with_the_organisation() {

        OrganisationCreationRequest organisationCreationRequest = someMinimalOrganisationRequest()
                .pbaAccounts(asList(aPbaPaymentAccount()
                        .pbaNumber("pbaNumber-1")
                        .build()))
                .superUser(aUserCreationRequest()
                        .firstName("some-fname")
                        .lastName("some-lname")
                        .email("someone@somewhere.com")
                        .pbaAccount(aPbaPaymentAccount()
                                .pbaNumber("pbaNumber-2")
                                .build())
                        .build())
                .build();

        Map<String, Object> response =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);


        assertThat(response.get("http_status")).isEqualTo("400");

        assertThat(paymentAccountRepository.findAll().size()).isEqualTo(0);
        assertThat(organisationRepository.findAll().size()).isEqualTo(0);
        assertThat(professionalUserRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void returns_bad_request_when_superuser_pba_number_null() {

        OrganisationCreationRequest organisationCreationRequest = someMinimalOrganisationRequest()
                .superUser(aUserCreationRequest()
                        .firstName("some-fname")
                        .lastName("some-lname")
                        .email("someone@somewhere.com")
                        .pbaAccount(aPbaPaymentAccount()
                                .pbaNumber(null)
                                .build())
                        .build())
                .build();

        Map<String, Object> response =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);


        assertThat(response.get("http_status")).isEqualTo("400");

        assertThat(paymentAccountRepository.findAll().size()).isEqualTo(0);
        assertThat(organisationRepository.findAll().size()).isEqualTo(0);
        assertThat(professionalUserRepository.findAll().size()).isEqualTo(0);
    }

}
