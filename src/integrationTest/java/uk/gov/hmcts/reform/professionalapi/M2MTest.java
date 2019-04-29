package uk.gov.hmcts.reform.professionalapi;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.reform.professionalapi.domain.entities.Organisation;
import uk.gov.hmcts.reform.professionalapi.domain.entities.PaymentAccount;
import uk.gov.hmcts.reform.professionalapi.domain.entities.ProfessionalUser;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.OrganisationRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.PaymentAccountRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.ProfessionalUserRepository;
import uk.gov.hmcts.reform.professionalapi.util.Service2ServiceEnabledIntegrationTest;

public class M2MTest extends Service2ServiceEnabledIntegrationTest {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ProfessionalUserRepository professionalUserRepository;

    @Autowired
    private PaymentAccountRepository paymentAccountRepository;

    @Before
    public void setUp() {
        professionalUserRepository.deleteAll();
        paymentAccountRepository.deleteAll();
        organisationRepository.deleteAll();
    }

    @Test
    public void persists_organisation_with_valid_super_user_payment_account() {

        Organisation persistedOrganisation = organisationRepository.saveAndFlush(
                new Organisation("x", "PENDING"));

        ProfessionalUser persistedProfessionalUser1 = professionalUserRepository.save(
                new ProfessionalUser("f", "l", "e", "s", persistedOrganisation));
        ProfessionalUser persistedProfessionalUser2 = professionalUserRepository.save(
                new ProfessionalUser("f", "l", "e", "s", persistedOrganisation));


        PaymentAccount persistedPaymentAccount1 = paymentAccountRepository.save(
                new PaymentAccount("p1"));
        PaymentAccount persistedPaymentAccount2 = paymentAccountRepository.save(
                new PaymentAccount("p2"));

        persistedProfessionalUser1.addPaymentAccount(persistedPaymentAccount1);
        persistedProfessionalUser1.addPaymentAccount(persistedPaymentAccount2);

        persistedProfessionalUser2.addPaymentAccount(persistedPaymentAccount1);
        persistedProfessionalUser2.addPaymentAccount(persistedPaymentAccount2);

        professionalUserRepository.save(persistedProfessionalUser1);
        professionalUserRepository.save(persistedProfessionalUser2);


        paymentAccountRepository.findAll()
                .forEach(paymentAccount -> assertThat(paymentAccount.getUsers().size())
                        .isEqualTo(2));

        professionalUserRepository.findAll()
                .forEach(professionalUser -> assertThat(professionalUser.getPaymentAccounts().size())
                        .isEqualTo(2));
    }


}
