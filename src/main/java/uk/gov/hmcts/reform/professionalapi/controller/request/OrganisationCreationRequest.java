package uk.gov.hmcts.reform.professionalapi.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.reform.professionalapi.util.PbaAccountUtil;

@Getter
@Setter
@Builder(builderMethodName = "anOrganisationCreationRequest")
public class OrganisationCreationRequest {

    @NotNull
    private final String name;

    private String status;

    private final String sraId;

    private final Boolean sraRegulated;

    private final String companyNumber;

    private final String companyUrl;

    @NotNull
    private final UserCreationRequest superUser;

    private List<String> paymentAccount;

    @NotNull
    private List<ContactInformationCreationRequest> contactInformation;

    @JsonCreator
    public OrganisationCreationRequest(
            @JsonProperty("name") String name,
            @JsonProperty("status") String status,
            @JsonProperty("sraId") String sraId,
            @JsonProperty("sraRegulated") Boolean sraRegulated,
            @JsonProperty("companyNumber") String companyNumber,
            @JsonProperty("companyUrl") String companyUrl,
            @JsonProperty("superUser") UserCreationRequest superUser,
            @JsonProperty("paymentAccount") List<String> paymentAccount,
            @JsonProperty("contactInformation") List<ContactInformationCreationRequest> contactInformationRequest) {

        this.name = PbaAccountUtil.removeEmptySpaces(name);
        this.status = status;
        this.sraId = PbaAccountUtil.removeAllSpaces(sraId);
        this.sraRegulated = sraRegulated;
        this.companyNumber = PbaAccountUtil.removeAllSpaces(companyNumber);
        this.companyUrl = PbaAccountUtil.removeAllSpaces(companyUrl);
        this.superUser = superUser;
        this.paymentAccount = paymentAccount.stream().map(pba -> PbaAccountUtil.removeEmptySpaces(pba)).collect(Collectors.toList());
        this.contactInformation = contactInformationRequest;
    }
}