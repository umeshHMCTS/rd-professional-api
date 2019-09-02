package uk.gov.hmcts.reform.professionalapi;

import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.professionalapi.controller.request.NewUserCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.response.IdamStatus;
import uk.gov.hmcts.reform.professionalapi.idam.IdamClient;

import java.util.Map;

@RunWith(SpringIntegrationSerenityRunner.class)
@ActiveProfiles("functional")

public class AddRolestoActiveUserInOrgTest extends AuthorizationFunctionalTest {

    RequestSpecification bearerTokenForPuiUserManager;
    RequestSpecification bearerTokenForNonPuiUserManager;

    @Autowired
    AddNewUserTest AddNewUserTest;
    uk.gov.hmcts.reform.professionalapi.idam.IdamClient IdamClient;


    @Test
    public void ac1_an_PRD_ADMIN_can_add_roles_to_an_active_user_inside_the_same_organisation_INTERNAL_should_return_200() {
        String OrgId = createAndUpdateOrganisationToActive(hmctsAdmin);
        NewUserCreationRequest newUserCreationRequest = professionalApiClient.createNewUserRequest();
        Map<String, Object> newUserResponse = professionalApiClient.addNewUserToAnOrganisation(OrgId, hmctsAdmin,newUserCreationRequest);
        bearerTokenForPuiUserManager = professionalApiClient.getMultipleAuthHeadersExternal(puiUserManager, newUserCreationRequest.getFirstName(), newUserCreationRequest.getFirstName(), newUserCreationRequest.getEmail());


        professionalApiClient.addNewRoleToActiveUser(bearerTokenForPuiUserManager);
    }

    @Test
    public void ac2_an_PRD_ADMIN_can_add_roles_to_an_active_user_inside_the_same_organisation_EXTERNAL_should_return_403() {
        String OrgId = createAndUpdateOrganisationToActive(hmctsAdmin);
        NewUserCreationRequest newUserCreationRequest = professionalApiClient.createNewUserRequest();
        Map<String, Object> newUserResponse = professionalApiClient.addNewUserToAnExternalOrganisation(hmctsAdmin,newUserCreationRequest);

//    @Test
//    public void ac3_find_all_status_users_for_an_organisation_with_pui_user_manager_should_return_200() {
//        Map<String, Object> response = professionalApiClient.searchAllActiveUsersByOrganisationExternal(HttpStatus.OK, generateBearerTokenForPuiManager(), "");
//        response.get("idamStatus").equals(IdamStatus.ACTIVE.toString());
//        validateUsers(response, false);
//    }
//
//    @Test
//    public void ac4_find_all_active_users_for_an_organisation_with_pui_user_manager_should_return_200() {
//        Map<String, Object> response = professionalApiClient.searchAllActiveUsersByOrganisationExternal(HttpStatus.OK, generateBearerTokenForPuiManager(), "Active");
//        response.get("idamStatus").equals(IdamStatus.ACTIVE.toString());
//        validateUsers(response, false);
//    }
//
//
//    @Test
//    public void ac5_find_all_pending_users_for_an_organisation_with_pui_user_manager_when_no_pending_user_exists_should_return_404() {
//        professionalApiClient.searchAllActiveUsersByOrganisationExternal(HttpStatus.NOT_FOUND, generateBearerTokenForPuiManager(), "Pending");
//    }
//
//    @Test
//    public void ac6_find_all_status_users_for_an_organisation_with_pui_user_manager_with_invalid_status_provided_should_return_400() {
//        professionalApiClient.searchAllActiveUsersByOrganisationExternal(HttpStatus.BAD_REQUEST, generateBearerTokenForPuiManager(), "INVALID");
//    }







    // I need to create a new organisation with a super user.
    // need to add an user to this organisation - this user must be active
    // I need to add roles to this user


}
}
