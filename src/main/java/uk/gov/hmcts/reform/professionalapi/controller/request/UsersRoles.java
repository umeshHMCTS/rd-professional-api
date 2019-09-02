package uk.gov.hmcts.reform.professionalapi.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import lombok.Getter;

@Getter
public class UsersRoles {

    @JsonProperty
    private final List<String> roles;

    @JsonCreator
    public UsersRoles (@JsonProperty("roles") List<String> roles) {

        this.roles = roles;
    }
}
