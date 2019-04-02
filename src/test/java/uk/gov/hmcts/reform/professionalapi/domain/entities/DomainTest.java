package uk.gov.hmcts.reform.professionalapi.domain.entities;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class DomainTest {

    @Test
    public void creates_domain_correctly() {

        Organisation organisation = mock(Organisation.class);

        Domain domain = new Domain("some-domain-name.com", organisation);

        assertThat(domain.getName()).isEqualTo("some-domain-name.com");
        assertThat(domain.getOrganisation()).isEqualTo(organisation);

        assertThat(domain.getId()).isNull(); // hibernate generated
        assertThat(domain.getCreated()).isNull(); // hibernate generated
        assertThat(domain.getLastUpdated()).isNull(); // hibernate generated
    }

}