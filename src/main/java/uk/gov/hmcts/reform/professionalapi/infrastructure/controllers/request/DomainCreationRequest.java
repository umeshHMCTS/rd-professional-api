package uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderMethodName = "aDomainCreationRequest")
public class DomainCreationRequest {

    @NotNull
    private final String domain;

    @JsonCreator
    public DomainCreationRequest(
            @JsonProperty("domain") String domain) {
        this.domain = domain;
    }
}
