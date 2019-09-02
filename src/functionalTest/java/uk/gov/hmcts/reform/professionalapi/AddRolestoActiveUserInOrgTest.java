package uk.gov.hmcts.reform.professionalapi;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.professionalapi.controller.request.NewUserCreationRequest;
import uk.gov.hmcts.reform.professionalapi.idam.IdamClient;

import java.util.Map;



import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
        import org.junit.BeforeClass;
        import org.junit.Test;
        import org.junit.runner.RunWith;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.test.context.ActiveProfiles;
        import uk.gov.hmcts.reform.professionalapi.controller.request.NewUserCreationRequest;
        import uk.gov.hmcts.reform.professionalapi.idam.IdamClient;

        import java.util.Map;

        import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringIntegrationSerenityRunner.class)
@ActiveProfiles("functional")



public class AddRolestoActiveUserInOrg extends AuthorizationFunctionalTest {

    @Autowired
    AddNewUserTest AddNewUserTest;
    uk.gov.hmcts.reform.professionalapi.idam.IdamClient IdamClient;

    @BeforeClass
    public void setup(){

        String OrgId = createAndUpdateOrganisationToActive(hmctsAdmin);
        NewUserCreationRequest newUserCreationRequest = professionalApiClient.createNewUserRequest();
        Map<String, Object> newUserResponse = professionalApiClient.addNewUserToAnOrganisation(OrgId, hmctsAdmin,newUserCreationRequest);
        newUserResponse




    }







    // I need to create a new organisation with a super user.
    // need to add an user to this organisation - this user must be active
    // I need to add roles to this user


}
}
