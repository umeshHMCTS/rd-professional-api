package uk.gov.hmcts.reform.professionalapi;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest.anOrganisationCreationRequest;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.PbaAccountCreationRequest.aPbaPaymentAccount;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.UserCreationRequest.aUserCreationRequest;
import static uk.gov.hmcts.reform.professionalapi.utils.OrganisationFixtures.someMinimalOrganisationRequest;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.ContactInformationCreationRequest.aContactInformationCreationRequest;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.professionalapi.domain.entities.Organisation;
import uk.gov.hmcts.reform.professionalapi.domain.entities.PaymentAccount;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.ContactInformationRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.OrganisationRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.PaymentAccountRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.ProfessionalUserRepository;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.ContactInformationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.util.ProfessionalReferenceDataClient;
import uk.gov.hmcts.reform.professionalapi.util.Service2ServiceEnabledIntegrationTest;

public class CreateOrganisationWithPaymentAccountTest extends Service2ServiceEnabledIntegrationTest {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ProfessionalUserRepository professionalUserRepository;

    @Autowired
    private PaymentAccountRepository paymentAccountRepository;

    @Autowired
    private ContactInformationRepository contactInformationRepository;

    private ProfessionalReferenceDataClient professionalReferenceDataClient;

    @Before
    public void setUp() {
        professionalReferenceDataClient = new ProfessionalReferenceDataClient(port);
        professionalUserRepository.deleteAll();
        paymentAccountRepository.deleteAll();
        contactInformationRepository.deleteAll();
        organisationRepository.deleteAll();
    }

    @Test
    public void persists_and_returns_a_single_pba_account_number_for_an_organisation() {

        OrganisationCreationRequest organisationCreationRequest = anOrganisationCreationRequest()
                .name("some-org-name")
                .pbaAccounts(asList(aPbaPaymentAccount()
                        .pbaNumber("pbaNumber-1")
                        .build()))
                .superUser(aUserCreationRequest()
                        .firstName("some-fname")
                        .lastName("some-lname")
                        .email("someone@somewhere.com")
                        .build())
                .contactInformation(Arrays.asList(aContactInformationCreationRequest().addressLine1("addressLine1").build()))
                .build();

        Map<String, Object> response =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);

        String orgIdentifierResponse = (String) response.get("organisationIdentifier");

        List<PaymentAccount> persistedPaymentAccounts = paymentAccountRepository.findAll();

        assertThat(persistedPaymentAccounts.size()).isEqualTo(1);
        assertThat(persistedPaymentAccounts.get(0).getOrganisation().getName())
                .isEqualTo("some-org-name");
    }

    @Test
    public void persists_and_returns_multiple_pba_account_numbers_for_an_organisation() {

       OrganisationCreationRequest organisationCreationRequest = anOrganisationCreationRequest()
                .name("some-org-name")
                .sraId("sra-id")
                .sraRegulated(Boolean.FALSE)
                .companyUrl("company-url")
                .companyNumber("companyn")
                .superUser(aUserCreationRequest()
                        .firstName("some-fname")
                        .lastName("some-lname")
                        .email("someone@somewhere.com")
                        .build())
                .pbaAccounts(Arrays.asList(
                        aPbaPaymentAccount().pbaNumber("pbaNumber-1").build(),
                        aPbaPaymentAccount().pbaNumber("pbaNumber-2").build(),
                        aPbaPaymentAccount().pbaNumber("pbaNumber-3").build())
                )
                .contactInformation(Arrays.asList(aContactInformationCreationRequest().addressLine1("addressLine1").build()))
                .build();

        Map<String, Object> createOrganisationResponse =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);

        List<PaymentAccount> persistedPaymentAccounts = paymentAccountRepository.findAll();

        assertThat(createOrganisationResponse.get("http_status")).isEqualTo("201");

        assertThat(persistedPaymentAccounts).extracting(acc -> acc.getPbaNumber())
                .containsExactlyInAnyOrder(
                        "pbaNumber-1",
                        "pbaNumber-2",
                        "pbaNumber-3");

        assertThat(persistedPaymentAccounts)
                .extracting(paymentAccount -> paymentAccount.getOrganisation().getName())
                .containsExactly(
                        organisationCreationRequest.getName(),
                        organisationCreationRequest.getName(),
                        organisationCreationRequest.getName());


    }

    @Test
    public void still_persists_organisation_when_payment_accounts_list_is_empty() {

       OrganisationCreationRequest organisationCreationRequest =
                someMinimalOrganisationRequest()
                        .pbaAccounts(emptyList())
                        .contactInformation(Arrays.asList(aContactInformationCreationRequest().addressLine1("addressLine1").build()))
                        .build();

        Map<String, Object> createOrganisationResponse =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);
        List<PaymentAccount> persistedPaymentAccounts = paymentAccountRepository.findAll();

        String orgIdentifierResponse = (String) createOrganisationResponse.get("organisationIdentifier");

        Organisation persistedOrganisation = organisationRepository
                .findByOrganisationIdentifier(UUID.fromString(orgIdentifierResponse));

        assertThat(createOrganisationResponse.get("http_status")).isEqualTo("201");

        assertThat(persistedPaymentAccounts).isEmpty();

        assertThat(persistedOrganisation.getName())
                .isEqualTo(organisationCreationRequest.getName());
    }

    @Test
    public void still_persists_organisation_when_payment_accounts_list_is_null() {

        OrganisationCreationRequest organisationCreationRequest =
                someMinimalOrganisationRequest()
                        .pbaAccounts(null)
                        .build();

        Map<String, Object> createOrganisationResponse =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);

        String orgIdentifierResponse = (String) createOrganisationResponse.get("organisationIdentifier");

        List<PaymentAccount> persistedPaymentAccounts = paymentAccountRepository.findAll();

        Organisation persistedOrganisation = organisationRepository
                .findByOrganisationIdentifier(UUID.fromString(orgIdentifierResponse));


        assertThat(createOrganisationResponse.get("http_status")).isEqualTo("201");

        assertThat(persistedPaymentAccounts).isEmpty();

        assertThat(persistedOrganisation.getName())
                .isEqualTo(organisationCreationRequest.getName());
    }

    @Test
    public void returns_400_when_a_null_pba_number_is_received() {

        OrganisationCreationRequest organisationCreationRequest =
                someMinimalOrganisationRequest()
                        .pbaAccounts(Arrays.asList(
                                aPbaPaymentAccount().pbaNumber("pbaNumber-1").build(),
                                aPbaPaymentAccount().pbaNumber(null).build()))
                        .build();

        Map<String, Object> createOrganisationResponse =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);


        assertThat(createOrganisationResponse.get("http_status")).isEqualTo("400");

        assertThat(paymentAccountRepository.findAll()).isEmpty();

        assertThat(organisationRepository.findAll()).isEmpty();
    }

}
