package uk.gov.hmcts.reform.professionalapi.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.reform.professionalapi.domain.LanguagePreference;
import uk.gov.hmcts.reform.professionalapi.domain.UserCategory;
import uk.gov.hmcts.reform.professionalapi.domain.UserType;
import uk.gov.hmcts.reform.professionalapi.util.PbaAccountUtil;

@Setter
@Getter
@Builder(builderMethodName = "anUserProfileCreationRequest")
public class UserProfileCreationRequest  {

    @JsonIgnore
    private final String emailRegex = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&’*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";

    @Pattern(regexp = emailRegex)
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private LanguagePreference languagePreference;

    @NotBlank
    private UserCategory userCategory;

    @NotBlank
    private UserType userType;

    @NotEmpty
    private List<String> roles;

    @JsonCreator
    public UserProfileCreationRequest(@JsonProperty(value = "email") String email,
                                      @JsonProperty(value = "firstName") String firstName,
                                      @JsonProperty(value = "lastName") String lastName,
                                      @JsonProperty(value = "languagePreference") LanguagePreference languagePreference,
                                      @JsonProperty(value = "userCategory") UserCategory userCategory,
                                      @JsonProperty(value = "userType") UserType userType,
                                      @JsonProperty(value = "roles") List<String> roles) {

        this.email = PbaAccountUtil.removeAllSpaces(email);
        this.firstName = PbaAccountUtil.removeEmptySpaces(firstName);
        this.lastName = PbaAccountUtil.removeEmptySpaces(lastName);
        this.languagePreference = languagePreference;
        this.userCategory = userCategory;
        this.userType = userType;
        this.roles =  roles.stream().map(r -> PbaAccountUtil.removeEmptySpaces(r)).collect(Collectors.toList());
    }
}
