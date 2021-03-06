package uk.gov.hmcts.reform.professionalapi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DeleteRoleResponseTest {

    @Test
    public void deleteRoleResponseTest() {
        RoleDeletionResponse deleteRoleResponse = new RoleDeletionResponse("Role","Code","Message");

        assertThat(deleteRoleResponse.getRoleName()).isEqualTo("Role");
        assertThat(deleteRoleResponse.getIdamStatusCode()).isEqualTo("Code");
        assertThat(deleteRoleResponse.getIdamMessage()).isEqualTo("Message");
    }
}