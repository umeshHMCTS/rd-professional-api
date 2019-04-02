package uk.gov.hmcts.reform.professionalapi.domain.service;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.professionalapi.domain.entities.Domain;
import uk.gov.hmcts.reform.professionalapi.domain.entities.Organisation;
import uk.gov.hmcts.reform.professionalapi.domain.entities.PaymentAccount;
import uk.gov.hmcts.reform.professionalapi.domain.entities.ProfessionalUser;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.DomainRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.OrganisationRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.PaymentAccountRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.ProfessionalUserRepository;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.DomainCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.PbaAccountCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.UserCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.response.OrganisationResponse;

public class OrganisationServiceTest {

    private final ProfessionalUserRepository professionalUserRepository = mock(ProfessionalUserRepository.class);
    private final PaymentAccountRepository paymentAccountRepository = mock(PaymentAccountRepository.class);
    private final OrganisationRepository organisationRepository = mock(OrganisationRepository.class);
    private final DomainRepository domainRepository = mock(DomainRepository.class);
    private final ProfessionalUser professionalUser = mock(ProfessionalUser.class);
    private final Organisation organisation = mock(Organisation.class);
    private final PaymentAccount paymentAccount = mock(PaymentAccount.class);
    private final Domain domain = mock(Domain.class);
    private final UserCreationRequest superUser = new UserCreationRequest(
            "some-fname",
            "some-lname",
            "some-email",
            null);
    private final List<DomainCreationRequest> domainCreationRequests = new ArrayList<>();
    private final List<PbaAccountCreationRequest> pbaAccountCreationRequests = new ArrayList<>();
    private final PbaAccountCreationRequest pbaAccountCreationRequest = new PbaAccountCreationRequest("pbaNumber-1");
    private final DomainCreationRequest domainCreationRequest = new DomainCreationRequest("some-domain.com");
    private final OrganisationService organisationService = new OrganisationService(
            organisationRepository,
            professionalUserRepository,
            paymentAccountRepository,
            domainRepository);

    @Before
    public void setUp() {

        pbaAccountCreationRequests.add(pbaAccountCreationRequest);

        domainCreationRequests.add(domainCreationRequest);

        when(organisation.getId()).thenReturn(UUID.randomUUID());

        when(professionalUserRepository.save(any(ProfessionalUser.class)))
                .thenReturn(professionalUser);

        when(organisationRepository.save(any(Organisation.class)))
                .thenReturn(organisation);

        when(paymentAccountRepository.save(any(PaymentAccount.class)))
                .thenReturn(paymentAccount);

        when(domainRepository.save(any(Domain.class)))
                .thenReturn(domain);

    }

    @Test
    public void saves_an_organisation() {

        OrganisationCreationRequest organisationCreationRequest =
                new OrganisationCreationRequest(
                        "some-org-name",
                        superUser,
                        domainCreationRequests,
                        pbaAccountCreationRequests);

        OrganisationResponse organisationResponse =
                organisationService.createOrganisationFrom(organisationCreationRequest);

        assertThat(organisationResponse).isNotNull();

        verify(
                organisationRepository,
                times(2)).save(any(Organisation.class));
        verify(
                professionalUserRepository,
                times(1)).save(any(ProfessionalUser.class));
        verify(
                paymentAccountRepository,
                times(1)).save(any(PaymentAccount.class));
        verify(
                domainRepository,
                times(1)).save(any(Domain.class));

        verify(
                organisation,
                times(1)).addDomain(domain);
        verify(
                organisation,
                times(1)).addPaymentAccount(paymentAccount);
    }


}