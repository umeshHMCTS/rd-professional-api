package uk.gov.hmcts.reform.professionalapi;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.DomainCreationRequest.aDomainCreationRequest;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest.anOrganisationCreationRequest;
import static uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.UserCreationRequest.aUserCreationRequest;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import uk.gov.hmcts.reform.professionalapi.domain.entities.Domain;
import uk.gov.hmcts.reform.professionalapi.domain.entities.Organisation;
import uk.gov.hmcts.reform.professionalapi.domain.entities.ProfessionalUser;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.util.Service2ServiceEnabledIntegrationTest;

public class CreateMinimalOrganisationTest extends Service2ServiceEnabledIntegrationTest {

    @Test
    public void persists_and_returns_valid_minimal_organisation() {

        OrganisationCreationRequest organisationCreationRequest = anOrganisationCreationRequest()
                .name("some-org-name")
                .superUser(aUserCreationRequest()
                        .firstName("some-fname")
                        .lastName("some-lname")
                        .email("someone@somewhere.com")
                        .build())
                .domains(asList(aDomainCreationRequest()
                                .domain("somewhere.com")
                                .build(),
                        aDomainCreationRequest()
                                .domain("somewhereelse.com")
                                .build()))
                .build();

        Map<String, Object> response =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);

        String organisationNameFromResponse = (String) response.get("name");

        Organisation persistedOrganisation = organisationRepository
                .findByName((String) response.get("name"));

        ProfessionalUser persistedSuperUser = persistedOrganisation.getUsers().get(0);

        assertThat(response.get("http_status")).isEqualTo("201");

        assertThat(persistedOrganisation.getName()).isEqualTo(organisationNameFromResponse);
        assertThat(persistedOrganisation.getStatus()).isEqualTo("PENDING");
        assertThat(persistedOrganisation.getUsers().size()).isEqualTo(1);
        assertThat(persistedOrganisation.getCreated())
                .isBeforeOrEqualTo(now())
                .isAfter(now().minusMinutes(1));
        assertThat(persistedOrganisation.getLastUpdated())
                .isBeforeOrEqualTo(now())
                .isAfter(now().minusMinutes(1));

        assertThat(persistedSuperUser.getEmailAddress()).isEqualTo("someone@somewhere.com");
        assertThat(persistedSuperUser.getFirstName()).isEqualTo("some-fname");
        assertThat(persistedSuperUser.getLastName()).isEqualTo("some-lname");
        assertThat(persistedSuperUser.getStatus()).isEqualTo("PENDING");
        assertThat(persistedSuperUser.getOrganisation().getName()).isEqualTo(organisationNameFromResponse);
        assertThat(persistedSuperUser.getCreated())
                .isBeforeOrEqualTo(now())
                .isAfter(now().minusMinutes(1));
        assertThat(persistedSuperUser.getLastUpdated())
                .isBeforeOrEqualTo(now())
                .isAfter(now().minusMinutes(1));

        assertThat(persistedOrganisation.getDomains().size()).isEqualTo(2);
        assertThat(persistedOrganisation.getDomains())
                .extracting("name")
                .containsExactlyInAnyOrder("somewhere.com", "somewhereelse.com");
        persistedOrganisation.getDomains().forEach(
                domain -> assertThat(domain.getCreated())
                        .isBeforeOrEqualTo(now())
                        .isAfter(now().minusMinutes(1))
        );
        persistedOrganisation.getDomains().forEach(
                domain -> assertThat(domain.getLastUpdated())
                        .isBeforeOrEqualTo(now())
                        .isAfter(now().minusMinutes(1))
        );

        assertThat(organisationNameFromResponse).isEqualTo("some-org-name");
        assertThat((List<String>) response.get("userIds"))
                .containsExactly(persistedSuperUser.getId().toString());
    }

    @Test
    public void returns_400_when_mandatory_data_not_present() {

        OrganisationCreationRequest organisationCreationRequest = anOrganisationCreationRequest()
                .name(null)
                .superUser(aUserCreationRequest()
                        .firstName("some-fname")
                        .lastName("some-lname")
                        .email("someone@somewhere.com")
                        .build())
                .build();

        Map<String, Object> response =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);

        assertThat(response.get("http_status")).isEqualTo("400");
        assertThat(response.get("response_body")).isEqualTo("");

        assertThat(organisationRepository.findAll()).isEmpty();
    }

    @Test
    public void returns_400_when_database_constraint_violated() {

        String organisationNameViolatingDatabaseMaxLengthConstraint = RandomStringUtils.random(256);

        OrganisationCreationRequest organisationCreationRequest = anOrganisationCreationRequest()
                .name(organisationNameViolatingDatabaseMaxLengthConstraint)
                .superUser(aUserCreationRequest()
                        .firstName("some-fname")
                        .lastName("some-lname")
                        .email("someone@somewhere.com")
                        .build())
                .build();

        Map<String, Object> response =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);

        assertThat(response.get("http_status")).isEqualTo("400");
        assertThat(response.get("response_body")).isEqualTo("");

        assertThat(organisationRepository.findAll()).isEmpty();
    }

    @Test
    public void returns_400_when_there_isnt_domain_matching_the_superusers_email() {

        OrganisationCreationRequest organisationCreationRequest = anOrganisationCreationRequest()
                .superUser(aUserCreationRequest()
                        .firstName("some-fname")
                        .lastName("some-lname")
                        .email("someone@somewhereelse.com")
                        .build())
                .domains(asList(aDomainCreationRequest()
                                .domain("somewhere.com")
                                .build(),
                        aDomainCreationRequest()
                                .domain("someotherplace.com")
                                .build()))
                .build();

        Map<String, Object> response =
                professionalReferenceDataClient.createOrganisation(organisationCreationRequest);

        assertThat(response.get("http_status")).isEqualTo("400");
        assertThat(response.get("response_body")).isEqualTo("");

        assertThat(organisationRepository.findAll()).isEmpty();
    }
}
