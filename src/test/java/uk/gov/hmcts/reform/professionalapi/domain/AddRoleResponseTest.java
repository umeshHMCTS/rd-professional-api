package uk.gov.hmcts.reform.professionalapi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class AddRoleResponseTest {

    @Test
    public void addRoleResponseTest() {
        RoleAdditionResponse addRoleResponse = new RoleAdditionResponse("Code","Message");

        assertThat(addRoleResponse.getIdamStatusCode()).isEqualTo("Code");
        assertThat(addRoleResponse.getIdamMessage()).isEqualTo("Message");
    }
}