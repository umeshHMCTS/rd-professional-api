package uk.gov.hmcts.reform.professionalapi.domain.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.professionalapi.domain.entities.*;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.DomainRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.OrganisationRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.PaymentAccountRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.ProfessionalUserRepository;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.DomainCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.PbaAccountCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.UserCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.response.OrganisationResponse;

@Service
@Slf4j
public class OrganisationService {

    private final OrganisationRepository organisationRepository;
    private final ProfessionalUserRepository professionalUserRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final DomainRepository domainRepository;

    public OrganisationService(
            OrganisationRepository organisationRepository,
            ProfessionalUserRepository professionalUserRepository,
            PaymentAccountRepository paymentAccountRepository,
            DomainRepository domainRepository) {

        this.organisationRepository = organisationRepository;
        this.professionalUserRepository = professionalUserRepository;
        this.paymentAccountRepository = paymentAccountRepository;
        this.domainRepository = domainRepository;
    }

    @Transactional
    public OrganisationResponse createOrganisationFrom(
            OrganisationCreationRequest organisationCreationRequest) {

        Organisation newOrganisation = new Organisation(
                organisationCreationRequest.getName(),
                OrganisationStatus.PENDING.name()
        );

        Organisation persistedOrganisation = organisationRepository.save(newOrganisation);

        addDomainToOrganisation(organisationCreationRequest.getDomains(), persistedOrganisation);

        addPbaAccountToOrganisation(organisationCreationRequest.getPbaAccounts(), persistedOrganisation);

        addSuperUserToOrganisation(organisationCreationRequest.getSuperUser(), persistedOrganisation);

        organisationRepository.save(persistedOrganisation);

        return new OrganisationResponse(persistedOrganisation);
    }

    private void addDomainToOrganisation(List<DomainCreationRequest> domainRequests, Organisation organisation) {

        domainRequests.forEach(domainRequest -> {
            Domain newDomain = new Domain(domainRequest.getDomain(), organisation);
            Domain persistedPaymentAccount = domainRepository.save(newDomain);
            organisation.addDomain(persistedPaymentAccount);
        });
    }

    private void addPbaAccountToOrganisation(
            List<PbaAccountCreationRequest> pbaAccountCreationRequest,
            Organisation organisation) {

        if (pbaAccountCreationRequest != null) {
            pbaAccountCreationRequest.forEach(pbaAccount -> {
                PaymentAccount paymentAccount = new PaymentAccount(pbaAccount.getPbaNumber());
                paymentAccount.setOrganisation(organisation);
                PaymentAccount persistedPaymentAccount = paymentAccountRepository.save(paymentAccount);
                organisation.addPaymentAccount(persistedPaymentAccount);
            });
        }
    }

    private void addSuperUserToOrganisation(
            UserCreationRequest userCreationRequest,
            Organisation organisation) {

        ProfessionalUser newProfessionalUser = new ProfessionalUser(
                userCreationRequest.getFirstName(),
                userCreationRequest.getLastName(),
                userCreationRequest.getEmail(),
                ProfessionalUserStatus.PENDING.name(),
                organisation);

        if (userCreationRequest.getPbaAccount() != null) {

            PaymentAccount paymentAccount = organisation.getPaymentAccounts().get(0);

            paymentAccount.setUser(newProfessionalUser);
        }

        ProfessionalUser persistedSuperUser = professionalUserRepository.save(newProfessionalUser);

        organisation.addProfessionalUser(persistedSuperUser);
    }
}
