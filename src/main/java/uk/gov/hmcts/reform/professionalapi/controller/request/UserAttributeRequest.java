package uk.gov.hmcts.reform.professionalapi.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import uk.gov.hmcts.reform.professionalapi.util.PbaAccountUtil;

@Getter
@Builder(builderMethodName = "aUserAttributeCreationRequest")
public class UserAttributeRequest {

    @JsonProperty
    private List<String> userRoles;

    @JsonCreator
    public UserAttributeRequest(
            @JsonProperty("userRoles") List<String> userRoles) {

        this.userRoles = userRoles.stream().map(r -> PbaAccountUtil.removeEmptySpaces(r)).collect(Collectors.toList());
    }

}
