package uk.gov.hmcts.reform.professionalapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.professionalapi.generator.ProfessionalApiGenerator.LENGTH_OF_ORGANISATION_IDENTIFIER;
import static uk.gov.hmcts.reform.professionalapi.generator.ProfessionalApiGenerator.generateUniqueAlphanumericId;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import feign.Response;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.professionalapi.controller.advice.ExternalApiException;
import uk.gov.hmcts.reform.professionalapi.controller.advice.ResourceNotFoundException;
import uk.gov.hmcts.reform.professionalapi.controller.feign.UserProfileFeignClient;
import uk.gov.hmcts.reform.professionalapi.controller.request.NewUserCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.request.RetrieveUserProfilesRequest;
import uk.gov.hmcts.reform.professionalapi.controller.response.GetUserProfileResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.IdamStatus;
import uk.gov.hmcts.reform.professionalapi.controller.response.NewUserResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.ProfessionalUsersEntityResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.ProfessionalUsersResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.UserProfile;
import uk.gov.hmcts.reform.professionalapi.domain.*;

import uk.gov.hmcts.reform.professionalapi.domain.ModifyUserRolesResponse;
import uk.gov.hmcts.reform.professionalapi.persistence.OrganisationRepository;
import uk.gov.hmcts.reform.professionalapi.persistence.PrdEnumRepository;
import uk.gov.hmcts.reform.professionalapi.persistence.ProfessionalUserRepository;
import uk.gov.hmcts.reform.professionalapi.persistence.UserAttributeRepository;
import uk.gov.hmcts.reform.professionalapi.service.impl.ProfessionalUserServiceImpl;
import uk.gov.hmcts.reform.professionalapi.service.impl.UserAttributeServiceImpl;

public class ProfessionalUserServiceTest {

    private final ProfessionalUserRepository professionalUserRepository = Mockito.mock(ProfessionalUserRepository.class);
    private final Organisation organisation = Mockito.mock(Organisation.class);
    private final OrganisationRepository organisationRepository = mock(OrganisationRepository.class);
    private final UserAttributeRepository userAttributeRepository = mock(UserAttributeRepository.class);
    private final PrdEnumRepository prdEnumRepository = mock(PrdEnumRepository.class);
    private final UserProfileFeignClient userProfileFeignClient = mock(UserProfileFeignClient.class);

    private final UserAttributeServiceImpl userAttributeService = mock(UserAttributeServiceImpl.class);

    private final ProfessionalUser professionalUserMock = mock(ProfessionalUser.class);

    private final FeignException feignExceptionMock = mock(FeignException.class);

    private final GetUserProfileResponse getUserProfileResponseMock = mock(GetUserProfileResponse.class);

    private final ProfessionalUser professionalUser = new ProfessionalUser("some-fname",
            "some-lname",
            "some-email",
            Mockito.mock(Organisation.class));

    private final SuperUser superUser = new SuperUser("some-fname",
            "some-lname",
            "some-super-email",
            Mockito.mock(Organisation.class));

    private List<ProfessionalUser> usersNonEmptyList = new ArrayList<ProfessionalUser>();

    private final ProfessionalUserServiceImpl professionalUserService = new ProfessionalUserServiceImpl(
            organisationRepository, professionalUserRepository, userAttributeRepository,
            prdEnumRepository, userAttributeService, userProfileFeignClient);

    private NewUserCreationRequest newUserCreationRequest;

    private  List<PrdEnum> prdEnums = new ArrayList<>();
    private List<String> userRoles;

    @Before
    public void setup() {
        userRoles = new ArrayList<>();
        userRoles.add("pui-user-manager");
        PrdEnumId prdEnumId = mock(PrdEnumId.class);
        PrdEnum anEnum = new PrdEnum(prdEnumId, "pui-user-manager", "SIDAM_ROLE");
        prdEnums.add(anEnum);

        newUserCreationRequest = new NewUserCreationRequest("first",
                "last",
                "domain@hotmail.com",
                userRoles,
                new ArrayList<>());
    }

    @Test
    public void retrieveUserByEmail() throws JsonProcessingException {
        String id = UUID.randomUUID().toString();
        superUser.setUserIdentifier(id);
        SuperUser superUserMock = mock(SuperUser.class);

        professionalUser.setUserIdentifier(id);
        PowerMockito.when(superUserMock.toProfessionalUser()).thenReturn(professionalUser);

        List<SuperUser> users = new ArrayList<>();
        users.add(superUser);
        List<String> roles = new ArrayList<>();
        roles.add("pui-case-manager");
        PowerMockito.when(professionalUser.getOrganisation().getStatus()).thenReturn(OrganisationStatus.ACTIVE);
        PowerMockito.when(organisation.getStatus()).thenReturn(OrganisationStatus.ACTIVE);
        PowerMockito.when(organisation.getUsers()).thenReturn(users);
        List<Organisation> organisations = new ArrayList<>();
        organisations.add(organisation);
        PowerMockito.when(professionalUserRepository.findByEmailAddress(any(String.class)))
                .thenReturn(professionalUser);

        UserProfile profile = new UserProfile(UUID.randomUUID().toString(), "email@org.com", "firstName", "lastName", IdamStatus.ACTIVE);

        GetUserProfileResponse userProfileResponse = new GetUserProfileResponse(profile, false);

        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(userProfileResponse);

        PowerMockito.when(getUserProfileResponseMock.getRoles()).thenReturn(roles);

        PowerMockito.when(userProfileFeignClient.getUserProfileById(anyString())).thenReturn(Response.builder().request(mock(Request.class)).body(body, Charset.defaultCharset()).status(200).build());

        ProfessionalUser user1 = professionalUserService.findProfessionalUserProfileByEmailAddress("email@org.com");
        assertEquals(professionalUser.getFirstName(), user1.getFirstName());
        assertEquals(professionalUser.getLastName(), user1.getLastName());
        assertEquals(professionalUser.getEmailAddress(), user1.getEmailAddress());
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void retrieveUserByEmail_EmptyData() throws JsonProcessingException {
        String id = UUID.randomUUID().toString();
        superUser.setUserIdentifier(id);
        SuperUser superUserMock = mock(SuperUser.class);

        professionalUser.setUserIdentifier(id);
        PowerMockito.when(superUserMock.toProfessionalUser()).thenReturn(professionalUser);

        List<SuperUser> users = new ArrayList<>();
        users.add(superUser);
        List<String> roles = new ArrayList<>();
        roles.add("pui-case-manager");
        PowerMockito.when(professionalUser.getOrganisation().getStatus()).thenReturn(OrganisationStatus.ACTIVE);
        PowerMockito.when(organisation.getStatus()).thenReturn(OrganisationStatus.ACTIVE);
        PowerMockito.when(organisation.getUsers()).thenReturn(users);
        List<Organisation> organisations = new ArrayList<>();
        organisations.add(organisation);
        PowerMockito.when(professionalUserRepository.findByEmailAddress(any(String.class)))
                .thenReturn(null);

        UserProfile profile = new UserProfile(UUID.randomUUID().toString(), "email@org.com", "firstName", "lastName", IdamStatus.ACTIVE);

        GetUserProfileResponse userProfileResponse = new GetUserProfileResponse(profile, false);

        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(userProfileResponse);

        PowerMockito.when(getUserProfileResponseMock.getRoles()).thenReturn(roles);

        PowerMockito.when(userProfileFeignClient.getUserProfileById(anyString())).thenReturn(Response.builder().request(mock(Request.class)).body(body, Charset.defaultCharset()).status(200).build());

        professionalUserService.findProfessionalUserProfileByEmailAddress("email@org.com");
    }


    @Test
    public void findUsersByOrganisation_with_deleted_users() throws Exception {

        ProfessionalUser user = mock(ProfessionalUser.class);
        String id = UUID.randomUUID().toString();
        List<String> ids = new ArrayList<>();
        when(user.getUserIdentifier()).thenReturn(id);
        ids.add(id);
        RetrieveUserProfilesRequest retrieveUserProfilesRequest = new RetrieveUserProfilesRequest(ids);
        List<ProfessionalUser> users = new ArrayList<>();
        users.add(user);

        ProfessionalUsersEntityResponse professionalUsersEntityResponse = new ProfessionalUsersEntityResponse();
        List<ProfessionalUsersResponse> userProfiles = new ArrayList<>();

        ProfessionalUser profile = new ProfessionalUser("firstName", "lastName", "email@org.com", organisation);

        ProfessionalUsersResponse userProfileResponse = new ProfessionalUsersResponse(profile);
        userProfileResponse.setUserIdentifier(id);
        userProfiles.add(userProfileResponse);
        professionalUsersEntityResponse.setUserProfiles(userProfiles);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String body = mapper.writeValueAsString(professionalUsersEntityResponse);

        when(professionalUserRepository.findByOrganisation(organisation)).thenReturn(users);
        when(userProfileFeignClient.getUserProfiles(any(),any(),any())).thenReturn(Response.builder().request(mock(Request.class)).body(body, Charset.defaultCharset()).status(200).build());

        ResponseEntity responseEntity = professionalUserService.findProfessionalUsersByOrganisation(organisation, "false", true, "");
        Mockito.verify(
                professionalUserRepository,
                Mockito.times(1)).findByOrganisation(organisation);

        assertThat(responseEntity).isNotNull();
    }

    @Test
    public void findUsersByOrganisation_with_status_active() throws Exception {
        Organisation organisationMock = mock(Organisation.class);

        ProfessionalUsersResponse professionalUsersResponse = new ProfessionalUsersResponse(new ProfessionalUser("fName","lName", "some@email.com", organisationMock));
        ProfessionalUsersResponse professionalUsersResponse1 = new ProfessionalUsersResponse(new ProfessionalUser("fName1","lName1", "some1@email.com", organisationMock));
        ProfessionalUsersResponse professionalUsersResponse2 = new ProfessionalUsersResponse(new ProfessionalUser("fName2","lName2", "some2@email.com", organisationMock));
        professionalUsersResponse.setIdamStatus(IdamStatus.ACTIVE.toString());
        professionalUsersResponse1.setIdamStatus(IdamStatus.ACTIVE.toString());
        professionalUsersResponse2.setIdamStatus(IdamStatus.PENDING.toString());
        List<ProfessionalUsersResponse> userProfiles = new ArrayList<>();
        userProfiles.add(professionalUsersResponse);
        userProfiles.add(professionalUsersResponse1);
        userProfiles.add(professionalUsersResponse2);
        ProfessionalUsersEntityResponse professionalUsersEntityResponse = new ProfessionalUsersEntityResponse();
        professionalUsersEntityResponse.setUserProfiles(userProfiles);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String body = mapper.writeValueAsString(professionalUsersEntityResponse);

        Response response = Response.builder().request(mock(Request.class)).body(body, Charset.defaultCharset()).status(200).build();

        ProfessionalUser user = mock(ProfessionalUser.class);
        List<ProfessionalUser> users = new ArrayList<>();
        users.add(user);
        when(professionalUserRepository.findByOrganisation(organisation)).thenReturn(users);

        when(userProfileFeignClient.getUserProfiles(any(),any(),any())).thenReturn(response);

        ResponseEntity responseEntity = professionalUserService.findProfessionalUsersByOrganisation(organisation, "false", true, "Active");
        Mockito.verify(
                professionalUserRepository,
                Mockito.times(1)).findByOrganisation(organisation);

        assertThat(responseEntity).isNotNull();
    }

    @Test
    public void modify_user_roles() throws Exception {

        ModifyUserProfileData modifyUserProfileData = new ModifyUserProfileData();
        Set<RoleName> roles = new HashSet<RoleName>();
        RoleName roleName1 = new RoleName("pui-case-manager");
        RoleName roleName2 = new RoleName("pui-case-organisation");
        roles.add(roleName1);
        roles.add(roleName2);
        modifyUserProfileData.setRolesAdd(roles);


        ModifyUserRolesResponse modifyUserRolesResponse = new ModifyUserRolesResponse();
        modifyUserRolesResponse.setAddRolesResponse(createAddRoleResponse(HttpStatus.OK, "Success"));
        modifyUserRolesResponse.setDeleteRolesResponse(createDeleteRoleResponse(HttpStatus.OK, "Success"));

        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(modifyUserRolesResponse);

        ObjectMapper mapper1 = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String body1 = mapper.writeValueAsString(modifyUserRolesResponse);

        when(userProfileFeignClient.modifyUserRoles(any(),any(), any())).thenReturn(Response.builder().request(mock(Request.class)).body(body, Charset.defaultCharset()).status(200).build());
        String id = UUID.randomUUID().toString();
        ModifyUserRolesResponse response = professionalUserService.modifyRolesForUser(modifyUserProfileData, id);

        assertThat(response).isNotNull();
        assertThat(response.getAddRolesResponse()).isNotNull();
        assertThat(response.getAddRolesResponse().getIdamMessage()).isEqualTo("Success");
        assertThat(response.getDeleteRolesResponse()).isNotNull();
        assertThat(response.getDeleteRolesResponse().get(0).getIdamMessage()).isEqualTo("Success");
    }

    @Test
    public void modify_user_roles_bad_request() throws Exception {

        ModifyUserProfileData modifyUserProfileData = new ModifyUserProfileData();
        Set<RoleName> roles = new HashSet<>();
        RoleName roleName1 = new RoleName("pui-case-manager");
        RoleName roleName2 = new RoleName("pui-case-organisation");
        roles.add(roleName1);
        roles.add(roleName2);
        modifyUserProfileData.setRolesAdd(roles);
        String id = UUID.randomUUID().toString();

        ModifyUserRolesResponse modifyUserRolesResponse = new ModifyUserRolesResponse();
        modifyUserRolesResponse.setAddRolesResponse(createAddRoleResponse(HttpStatus.BAD_REQUEST, "Request Not Valid"));
        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(modifyUserRolesResponse);

        when(userProfileFeignClient.modifyUserRoles(any(), any(), any())).thenReturn(Response.builder().request(mock(Request.class)).body(body, Charset.defaultCharset()).status(400).build());

        ModifyUserRolesResponse response = professionalUserService.modifyRolesForUser(modifyUserProfileData, id);

        assertThat(response).isNotNull();
        assertThat(response.getAddRolesResponse()).isNotNull();
        assertThat(response.getAddRolesResponse().getIdamMessage()).isEqualTo("Request Not Valid");
    }

    @Test(expected = ExternalApiException.class)
    public void modify_user_roles_server_error() throws Exception {

        ModifyUserProfileData modifyUserProfileData = new ModifyUserProfileData();
        Set<RoleName> roles = new HashSet<>();
        RoleName roleName1 = new RoleName("pui-case-manager");
        RoleName roleName2 = new RoleName("pui-case-organisation");
        roles.add(roleName1);
        roles.add(roleName2);
        modifyUserProfileData.setRolesAdd(roles);



        ModifyUserRolesResponse modifyUserRolesResponse = new ModifyUserRolesResponse();
        modifyUserRolesResponse.setAddRolesResponse(createAddRoleResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
        ObjectMapper mapper = new ObjectMapper();

        String body = mapper.writeValueAsString(modifyUserRolesResponse);

        when(feignExceptionMock.status()).thenReturn(500);
        when(userProfileFeignClient.modifyUserRoles(any(), any(), any())).thenThrow(feignExceptionMock);
        String id = UUID.randomUUID().toString();

        ModifyUserRolesResponse response = professionalUserService.modifyRolesForUser(modifyUserProfileData, id);

        assertThat(response).isNotNull();
        assertThat(response.getAddRolesResponse()).isNotNull();
        assertThat(response.getAddRolesResponse().getIdamMessage()).isEqualTo("Internal Server Error");
    }


    @Test
    public void addNewUserToAnOrganisation() {

        when(organisation.getOrganisationIdentifier()).thenReturn(generateUniqueAlphanumericId(LENGTH_OF_ORGANISATION_IDENTIFIER));
        when(professionalUserRepository.save(any(ProfessionalUser.class))).thenReturn(professionalUser);

        NewUserResponse newUserResponse = professionalUserService.addNewUserToAnOrganisation(professionalUser, userRoles, prdEnums);
        assertThat(newUserResponse).isNotNull();

        verify(professionalUserRepository, times(1)).save(any(ProfessionalUser.class));
        verify(userAttributeService, times(1)).addUserAttributesToUser(any(ProfessionalUser.class), (Mockito.anyList()), (Mockito.anyList()));
    }

    @Test(expected = ExternalApiException.class)
    public void findUsersByOrganisationEmptyResultExceptionTest()throws Exception {
        ProfessionalUser user = mock(ProfessionalUser.class);
        String id = UUID.randomUUID().toString();
        List<String> ids = new ArrayList<>();
        when(user.getUserIdentifier()).thenReturn(id);
        ids.add(id);
        RetrieveUserProfilesRequest retrieveUserProfilesRequest = new RetrieveUserProfilesRequest(ids);
        List<ProfessionalUser> users = new ArrayList<>();
        users.add(user);

        FeignException exceptionMock = mock(FeignException.class);
        when(exceptionMock.status()).thenReturn(500);
        ProfessionalUsersEntityResponse professionalUsersEntityResponse = new ProfessionalUsersEntityResponse();
        List<ProfessionalUsersResponse> userProfiles = new ArrayList<>();

        when(professionalUserRepository.findByOrganisation(organisation)).thenReturn(users);
        when(userProfileFeignClient.getUserProfiles(any(),any(),any())).thenThrow(exceptionMock);

        ResponseEntity responseEntity = professionalUserService.findProfessionalUsersByOrganisation(organisation, "false", true, "");
        Mockito.verify(
                professionalUserRepository,
                Mockito.times(1)).findByOrganisation(organisation);

        assertThat(responseEntity).isNotNull();
    }

    @Test
    public void shouldPersistUser() {

        when(professionalUserRepository.save(any(ProfessionalUser.class))).thenReturn(professionalUser);

        ProfessionalUser actualProfessionalUser = professionalUserService.persistUser(professionalUser);

        assertThat(actualProfessionalUser).isNotNull();

        verify(professionalUserRepository, times(1)).save(any(ProfessionalUser.class));
    }

    @Test
    public void shouldReturnProfessionalUserByEmail() {
        when(professionalUserRepository.findByEmailAddress("some@email.com")).thenReturn(professionalUserMock);

        ProfessionalUser user = professionalUserService.findProfessionalUserByEmailAddress("some@email.com");
        assertThat(user).isNotNull();
    }

    @Test
    public void shouldReturnProfessionalUserById() {

        UUID id = UUID.randomUUID();
        ProfessionalUser professionalUserMock = mock(ProfessionalUser.class);

        Optional<ProfessionalUser> professionalUserOptional = Optional.of(professionalUserMock);

        when(professionalUserRepository.findById(id)).thenReturn(professionalUserOptional);

        ProfessionalUser professionalUserResponse = professionalUserService.findProfessionalUserById(id);
        assertThat(professionalUserResponse).isNotNull();
    }

    @Test
    public void shouldReturnProfessionalUserByIdShouldReturnNullIfUserNotFound() {
        UUID id = UUID.randomUUID();
        Optional<ProfessionalUser> professionalUserOptional = Optional.empty();

        when(professionalUserRepository.findById(id)).thenReturn(professionalUserOptional);

        ProfessionalUser professionalUserResponse = professionalUserService.findProfessionalUserById(id);
        assertThat(professionalUserResponse).isNull();
    }

    private AddRoleResponse createAddRoleResponse(HttpStatus status, String message) {

        AddRoleResponse addRoleResponse = new AddRoleResponse();
        addRoleResponse.setIdamStatusCode(status.toString());
        addRoleResponse.setIdamMessage(message);
        return addRoleResponse;
    }

    private List<DeleteRoleResponse> createDeleteRoleResponse(HttpStatus status, String message) {

        DeleteRoleResponse deleteRoleResponse = new DeleteRoleResponse();
        deleteRoleResponse.setIdamStatusCode(status.toString());
        deleteRoleResponse.setIdamMessage(message);
        List<DeleteRoleResponse> deleteRoleResponses = new ArrayList<>();
        deleteRoleResponses.add(deleteRoleResponse);
        return deleteRoleResponses;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnUsersInResponseEntityWithPageable() throws JsonProcessingException {
        Pageable pageableMock = mock(Pageable.class);
        Organisation organisationMock = mock(Organisation.class);
        List<ProfessionalUser> professionalUserList = new ArrayList<>();
        Page<ProfessionalUser> professionalUserPage = (Page<ProfessionalUser>) mock(Page.class);

        ProfessionalUser professionalUser = new ProfessionalUser("fName","lName", "some@email.com", organisationMock);
        ProfessionalUser professionalUser1 = new ProfessionalUser("fName","lName", "some1@email.com", organisationMock);
        professionalUserList.add(professionalUser);
        professionalUserList.add(professionalUser1);

        when(professionalUserRepository.findByOrganisation(organisationMock, pageableMock)).thenReturn(professionalUserPage);
        when(professionalUserPage.getContent()).thenReturn(professionalUserList);

        ProfessionalUsersResponse professionalUsersResponse = new ProfessionalUsersResponse(professionalUser);
        ProfessionalUsersResponse professionalUsersResponse1 = new ProfessionalUsersResponse(professionalUser1);
        professionalUsersResponse.setIdamStatus(IdamStatus.ACTIVE.toString());
        professionalUsersResponse1.setIdamStatus(IdamStatus.ACTIVE.toString());
        List<ProfessionalUsersResponse> userProfiles = new ArrayList<>();
        userProfiles.add(professionalUsersResponse);
        userProfiles.add(professionalUsersResponse1);
        ProfessionalUsersEntityResponse professionalUsersEntityResponse = new ProfessionalUsersEntityResponse();
        professionalUsersEntityResponse.setUserProfiles(userProfiles);

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String body = mapper.writeValueAsString(professionalUsersEntityResponse);
        Response response = Response.builder().request(mock(Request.class)).body(body, Charset.defaultCharset()).status(200).build();
        when(userProfileFeignClient.getUserProfiles(any(),any(),any())).thenReturn(response);

        ResponseEntity responseEntity = professionalUserService.findProfessionalUsersByOrganisationWithPageable(organisationMock, "false", false, "Active", pageableMock);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().get("paginationInfo")).isNotEmpty();
        Mockito.verify(professionalUserRepository, Mockito.times(1)).findByOrganisation(organisationMock, pageableMock);
    }

    @Test(expected = ResourceNotFoundException.class)
    @SuppressWarnings("unchecked")
    public void shouldThrowResourceNotFoundExceptionWhenNoUsersReturned() {
        Pageable pageableMock = mock(Pageable.class);
        Organisation organisationMock = mock(Organisation.class);
        Page<ProfessionalUser> professionalUserPage = (Page<ProfessionalUser>) mock(Page.class);

        when(professionalUserRepository.findByOrganisation(organisationMock, pageableMock)).thenReturn(professionalUserPage);

        professionalUserService.findProfessionalUsersByOrganisationWithPageable(organisationMock, "false", false, "Active", pageableMock);
    }
}