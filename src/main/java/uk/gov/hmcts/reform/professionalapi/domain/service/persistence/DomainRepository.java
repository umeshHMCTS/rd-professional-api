package uk.gov.hmcts.reform.professionalapi.domain.service.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.professionalapi.domain.entities.Domain;

public interface DomainRepository extends JpaRepository<Domain, UUID> {

    Domain findByName(String name);
}
