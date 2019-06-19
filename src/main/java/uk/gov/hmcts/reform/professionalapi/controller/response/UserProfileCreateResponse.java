package uk.gov.hmcts.reform.professionalapi.controller.response;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UserProfileCreateResponse {

    public UUID getIdamId() {
        return idamId;
    }

    public void setIdamId(UUID idamId) {
        this.idamId = idamId;
    }

    public Integer getIdamRegistrationResponse() {
        return idamRegistrationResponse;
    }

    public void setIdamRegistrationResponse(Integer idamRegistrationResponse) {
        this.idamRegistrationResponse = idamRegistrationResponse;
    }

    private UUID idamId;
    private Integer idamRegistrationResponse;

    public boolean isUserCreated() {
        return getIdamRegistrationResponse() == HttpStatus.CREATED.value();
    }

}
