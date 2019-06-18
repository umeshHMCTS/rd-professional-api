package uk.gov.hmcts.reform.professionalapi.controller.response;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserProfileCreateResponse {

    private UUID idamId;
    private Integer idamRegistrationResponse;

}
