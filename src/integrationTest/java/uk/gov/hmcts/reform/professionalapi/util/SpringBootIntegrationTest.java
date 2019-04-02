package uk.gov.hmcts.reform.professionalapi.util;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.professionalapi.Application;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.DomainRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.OrganisationRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.PaymentAccountRepository;
import uk.gov.hmcts.reform.professionalapi.domain.service.persistence.ProfessionalUserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class SpringBootIntegrationTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected OrganisationRepository organisationRepository;

    @Autowired
    protected ProfessionalUserRepository professionalUserRepository;

    @Autowired
    protected DomainRepository domainRepository;

    @Autowired
    protected PaymentAccountRepository paymentAccountRepository;

    protected ProfessionalReferenceDataClient professionalReferenceDataClient;

    @Before
    public void setUp() {
        professionalReferenceDataClient = new ProfessionalReferenceDataClient(port);
        domainRepository.deleteAll();
        paymentAccountRepository.deleteAll();
        professionalUserRepository.deleteAll();
        organisationRepository.deleteAll();
    }
}
