package uk.gov.hmcts.reform.professionalapi.controller;

import feign.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.professionalapi.controller.advice.ErrorResponse;
import uk.gov.hmcts.reform.professionalapi.controller.feign.UserProfileFeignClient;
import uk.gov.hmcts.reform.professionalapi.controller.request.InvalidRequest;
import uk.gov.hmcts.reform.professionalapi.controller.request.NewUserCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.request.OrganisationCreationRequestValidator;
import uk.gov.hmcts.reform.professionalapi.controller.request.OrganisationIdentifierValidatorImpl;
import uk.gov.hmcts.reform.professionalapi.controller.request.ProfessionalUserReqValidator;
import uk.gov.hmcts.reform.professionalapi.controller.request.UpdateOrganisationRequestValidator;
import uk.gov.hmcts.reform.professionalapi.controller.request.UserCreationRequestValidator;
import uk.gov.hmcts.reform.professionalapi.controller.request.UserProfileCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.response.OrganisationPbaResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.OrganisationResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.OrganisationsDetailResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.ProfessionalUsersEntityResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.ProfessionalUsersResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.UserProfileCreationResponse;
import uk.gov.hmcts.reform.professionalapi.domain.LanguagePreference;
import uk.gov.hmcts.reform.professionalapi.domain.Organisation;
import uk.gov.hmcts.reform.professionalapi.domain.OrganisationStatus;
import uk.gov.hmcts.reform.professionalapi.domain.PrdEnum;
import uk.gov.hmcts.reform.professionalapi.domain.ProfessionalUser;
import uk.gov.hmcts.reform.professionalapi.domain.UserCategory;
import uk.gov.hmcts.reform.professionalapi.domain.UserType;
import uk.gov.hmcts.reform.professionalapi.service.OrganisationService;
import uk.gov.hmcts.reform.professionalapi.service.PaymentAccountService;
import uk.gov.hmcts.reform.professionalapi.service.PrdEnumService;
import uk.gov.hmcts.reform.professionalapi.service.ProfessionalUserService;
import uk.gov.hmcts.reform.professionalapi.util.JsonFeignResponseHelper;
import uk.gov.hmcts.reform.professionalapi.util.PbaAccountUtil;

@RestController
@Slf4j
public abstract class SuperController {

    @Autowired
    protected OrganisationService organisationService;
    @Autowired
    protected ProfessionalUserService professionalUserService;
    @Autowired
    protected PaymentAccountService paymentAccountService;
    @Autowired
    protected PrdEnumService prdEnumService;
    @Autowired
    protected UpdateOrganisationRequestValidator updateOrganisationRequestValidator;
    @Autowired
    protected OrganisationCreationRequestValidator organisationCreationRequestValidator;
    @Autowired
    protected OrganisationIdentifierValidatorImpl organisationIdentifierValidatorImpl;
    @Autowired
    protected ProfessionalUserReqValidator profExtUsrReqValidator;
    @Autowired
    private UserProfileFeignClient userProfileFeignClient;

    @Value("${exui.role.hmcts-admin:}")
    protected String prdAdmin;

    @Value("${exui.role.pui-user-manager:}")
    protected String puiUserManager;

    @Value("${exui.role.pui-organisation-manager:}")
    protected String puiOrgManager;

    @Value("${exui.role.pui-finance-manager}")
    protected String puiFinanceManager;

    @Value("${exui.role.pui-case-manager:}")
    protected String puiCaseManager;

    @Value("${prdEnumRoleType}")
    protected String prdEnumRoleType;

    protected ResponseEntity<OrganisationResponse>  createOrganisationFrom(OrganisationCreationRequest organisationCreationRequest) {

        organisationCreationRequestValidator.validate(organisationCreationRequest);

        OrganisationResponse organisationResponse =
                organisationService.createOrganisationFrom(organisationCreationRequest);

        log.info("Received response to create a new organisation..." + organisationResponse);
        return ResponseEntity
                .status(201)
                .body(organisationResponse);
    }

    protected ResponseEntity<?> retrieveAllOrganisationOrById(String organisationIdentifier, String status) {
        String orgId = PbaAccountUtil.removeEmptySpaces(organisationIdentifier);
        String orgStatus = PbaAccountUtil.removeEmptySpaces(status);

        Object organisationResponse = null;
        if (StringUtils.isEmpty(orgId) && StringUtils.isEmpty(orgStatus)) {
            log.info("Received request to retrieve all organisations");
            organisationResponse =
                    organisationService.retrieveOrganisations();

        } else if (StringUtils.isEmpty(orgStatus) && StringUtils.isNotEmpty(orgId)
                || (StringUtils.isNotEmpty(orgStatus) && StringUtils.isNotEmpty(orgId))) {
            log.info("Received request to retrieve organisation with ID ");

            organisationCreationRequestValidator.validateOrganisationIdentifier(orgId);
            organisationResponse =
                    organisationService.retrieveOrganisation(orgId);

        } else if (StringUtils.isNotEmpty(orgStatus) && StringUtils.isEmpty(orgId)) {

            if (organisationCreationRequestValidator.contains(orgStatus.toUpperCase())) {

                log.info("Received request to retrieve organisation with status " + orgStatus.toUpperCase());
                organisationResponse =
                        organisationService.findByOrganisationStatus(OrganisationStatus.valueOf(orgStatus.toUpperCase()));
            } else {
                log.error("Invalid Request param for status field");
                throw new InvalidRequest("400");
            }
        }
        log.debug("Received response to retrieve organisation details" + organisationResponse);
        return ResponseEntity
                .status(200)
                .body(organisationResponse);
    }

    protected ResponseEntity<ProfessionalUsersResponse> retrieveUserByEmail(String email) {

        ProfessionalUser user = professionalUserService.findProfessionalUserByEmailAddress(PbaAccountUtil.removeEmptySpaces(email));

        if (user == null || user.getOrganisation().getStatus() != OrganisationStatus.ACTIVE) {
            throw new EmptyResultDataAccessException(1);
        }
        return ResponseEntity
                .status(200)
                .body(new ProfessionalUsersResponse(user));
    }

    protected ResponseEntity<?> retrievePaymentAccountByUserEmail(String email) {

        Organisation organisation = paymentAccountService.findPaymentAccountsByEmail(PbaAccountUtil.removeEmptySpaces(email));
        if (null == organisation || organisation.getPaymentAccounts().isEmpty()) {

            throw new EmptyResultDataAccessException(1);
        }

        return ResponseEntity
                .status(200)
                .body(new OrganisationPbaResponse(organisation, false));
    }

    protected ResponseEntity<?> updateOrganisationById(OrganisationCreationRequest organisationCreationRequest, String organisationIdentifier) {
        String orgId = PbaAccountUtil.removeEmptySpaces(organisationIdentifier);

        organisationCreationRequestValidator.validate(organisationCreationRequest);
        organisationCreationRequestValidator.validateOrganisationIdentifier(orgId);
        Organisation existingOrganisation = organisationService.getOrganisationByOrgIdentifier(orgId);
        updateOrganisationRequestValidator.validateStatus(existingOrganisation, organisationCreationRequest.getStatus(), orgId);

        ProfessionalUser professionalUser = existingOrganisation.getUsers().get(0);
        if (existingOrganisation.getStatus().isPending() && organisationCreationRequest.getStatus() != null
             && organisationCreationRequest.getStatus().isActive()) {
            log.info("Organisation is getting activated");
            ResponseEntity responseEntity = createUserProfileFor(professionalUser, null, true);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                UserProfileCreationResponse userProfileCreationResponse = (UserProfileCreationResponse) responseEntity.getBody();
                log.info("Idam registration success !! idamId = " + userProfileCreationResponse.getIdamId());
                professionalUser.setUserIdentifier(userProfileCreationResponse.getIdamId());
                professionalUserService.persistUser(professionalUser);
            } else {
                log.error("Idam register user failed with status code : " + responseEntity.getStatusCode());
                return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
            }
        }
        OrganisationResponse organisationResponse = organisationService.updateOrganisation(organisationCreationRequest, orgId);
        return ResponseEntity.status(200).build();
    }

    private ResponseEntity createUserProfileFor(ProfessionalUser professionalUser, List<String> roles, boolean isAdminUser) {
        log.info("Creating user...");
        List<String> userRoles = isAdminUser ? prdEnumService.getPrdEnumByEnumType(prdEnumRoleType) : roles;
        UserProfileCreationRequest userCreationRequest = new UserProfileCreationRequest(
                PbaAccountUtil.removeAllSpaces(professionalUser.getEmailAddress()),
                PbaAccountUtil.removeEmptySpaces(professionalUser.getFirstName()),
                PbaAccountUtil.removeEmptySpaces(professionalUser.getLastName()),
                LanguagePreference.EN,
                UserCategory.PROFESSIONAL,
                UserType.EXTERNAL,
                userRoles);

        Response response = userProfileFeignClient.createUserProfile(userCreationRequest);

        Class clazz = response.status() > 300 ? ErrorResponse.class : UserProfileCreationResponse.class;
        return JsonFeignResponseHelper.toResponseEntity(response, clazz);
    }

    protected ResponseEntity<?> retrieveAllOrganisationsByStatus(String status) {
        String orgStatus = PbaAccountUtil.removeEmptySpaces(status);

        OrganisationsDetailResponse organisationsDetailResponse;
        if (organisationCreationRequestValidator.contains(orgStatus.toUpperCase())) {

            organisationsDetailResponse =
                    organisationService.findByOrganisationStatus(OrganisationStatus.valueOf(orgStatus.toUpperCase()));
        } else {
            log.error("Invalid Request param for status field");
            throw new InvalidRequest("400");
        }
        log.info("Received response for status...");
        return ResponseEntity.status(200).body(organisationsDetailResponse);
    }

    protected ResponseEntity<?> inviteUserToOrganisation(NewUserCreationRequest newUserCreationRequest, String organisationIdentifier) {
        String orgId = PbaAccountUtil.removeEmptySpaces(organisationIdentifier);

        Object responseBody = null;
        int responseStatus;

        organisationCreationRequestValidator.validateOrganisationIdentifier(orgId);
        Organisation existingOrganisation = organisationService.getOrganisationByOrgIdentifier(orgId);
        organisationCreationRequestValidator.isOrganisationActive(existingOrganisation);
        List<PrdEnum> prdEnumList = prdEnumService.findAllPrdEnums();
        List<String> roles = newUserCreationRequest.getRoles();
        UserCreationRequestValidator.validateRoles(roles, prdEnumList);

        ProfessionalUser newUser = new ProfessionalUser(newUserCreationRequest.getFirstName(), newUserCreationRequest.getLastName(), newUserCreationRequest.getEmail(), existingOrganisation);
        ResponseEntity responseEntity = createUserProfileFor(newUser, roles, false);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            UserProfileCreationResponse userProfileCreationResponse = (UserProfileCreationResponse) responseEntity.getBody();
            log.info("Idam registration success !! idamId = " + userProfileCreationResponse.getIdamId());
            newUser.setUserIdentifier(userProfileCreationResponse.getIdamId());
            responseBody = professionalUserService.addNewUserToAnOrganisation(newUser, roles, prdEnumList);
        } else {
            log.error("Idam register user failed with status code : " + responseEntity.getStatusCode());
            responseBody = responseEntity.getBody();
        }

        return ResponseEntity
                .status(responseEntity.getStatusCode().value())
                .body(responseBody);
    }

    protected ResponseEntity<ProfessionalUsersEntityResponse> searchUsersByOrganisation(String organisationIdentifier, String showDeleted) {
        organisationCreationRequestValidator.validateOrganisationIdentifier(organisationIdentifier);
        Organisation existingOrganisation = organisationService.getOrganisationByOrgIdentifier(organisationIdentifier);
        organisationIdentifierValidatorImpl.validate(existingOrganisation, null, organisationIdentifier);

        if (OrganisationStatus.ACTIVE != existingOrganisation.getStatus()) {
            log.error("Organisation is not Active hence not returning any users");
            throw new EmptyResultDataAccessException(1);
        }

        boolean showDeletedFlag = false;
        if ("True".equalsIgnoreCase(showDeleted)) {
            showDeletedFlag = true;
        }

        return ResponseEntity
                .status(200)
                .body(new ProfessionalUsersEntityResponse(professionalUserService
                        .findProfessionalUsersByOrganisation(existingOrganisation, showDeletedFlag)));
    }
}
