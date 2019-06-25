package uk.gov.hmcts.reform.sidam.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class UserResponse {
    private Boolean active;
    private String email;
    private String forename;
    private String id;
    private Boolean locked;
    private List<String> roles;
    private String surname;
}
