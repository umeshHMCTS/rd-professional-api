package uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderMethodName = "anOrganisationCreationRequest")
public class OrganisationCreationRequest {

    @NotNull
    private final String name;

    @NotNull
    private final UserCreationRequest superUser;

    @NotNull
    @Size(min = 1)
    private final List<DomainCreationRequest> domains;

    private final List<PbaAccountCreationRequest> pbaAccounts;

    @JsonCreator
    public OrganisationCreationRequest(

            @JsonProperty("name") String name,
            @JsonProperty("superUser") UserCreationRequest superUser,
            @JsonProperty("domains") List<DomainCreationRequest> domains,
            @JsonProperty("pbaAccounts") List<PbaAccountCreationRequest> pbaAccountCreationRequests) {

        this.name = name;
        this.superUser = superUser;
        this.domains = domains;
        this.pbaAccounts = pbaAccountCreationRequests;
    }
}

