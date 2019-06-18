package uk.gov.hmcts.reform.professionalapi.controller;

import static uk.gov.hmcts.reform.professionalapi.controller.request.UserProfileCreationRequest.anUserProfileCreationRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.professionalapi.controller.request.InvalidRequest;
import uk.gov.hmcts.reform.professionalapi.controller.request.NewUserCreationRequest;

import uk.gov.hmcts.reform.professionalapi.controller.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.request.OrganisationCreationRequestValidator;
import uk.gov.hmcts.reform.professionalapi.controller.request.UpdateOrganisationRequestValidator;
import uk.gov.hmcts.reform.professionalapi.controller.request.UserCreationRequestValidator;
import uk.gov.hmcts.reform.professionalapi.controller.request.UserProfileCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.response.NewUserResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.OrganisationPbaResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.OrganisationResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.OrganisationsDetailResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.ProfessionalUsersResponse;
import uk.gov.hmcts.reform.professionalapi.controller.response.UserProfileCreateResponse;
import uk.gov.hmcts.reform.professionalapi.domain.LanguagePreference;
import uk.gov.hmcts.reform.professionalapi.domain.Organisation;
import uk.gov.hmcts.reform.professionalapi.domain.OrganisationStatus;
import uk.gov.hmcts.reform.professionalapi.domain.PrdEnum;
import uk.gov.hmcts.reform.professionalapi.domain.ProfessionalUser;
import uk.gov.hmcts.reform.professionalapi.domain.UserCategory;
import uk.gov.hmcts.reform.professionalapi.domain.UserType;
import uk.gov.hmcts.reform.professionalapi.feign.UserProfileFeignClient;
import uk.gov.hmcts.reform.professionalapi.service.OrganisationService;
import uk.gov.hmcts.reform.professionalapi.service.PaymentAccountService;
import uk.gov.hmcts.reform.professionalapi.service.PrdEnumService;
import uk.gov.hmcts.reform.professionalapi.service.ProfessionalUserService;

@RequestMapping(
        path = "v1/organisations",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
@RestController
@Slf4j
@AllArgsConstructor
public class OrganisationController {

    private OrganisationService organisationService;
    private ProfessionalUserService professionalUserService;
    private PaymentAccountService paymentAccountService;
    private PrdEnumService prdEnumService;

    private UpdateOrganisationRequestValidator updateOrganisationRequestValidator;
    private OrganisationCreationRequestValidator organisationCreationRequestValidator;

    @Autowired
    private UserProfileFeignClient userProfileFeignClient;

    @ApiOperation(
            value = "Creates an organisation",
            authorizations = {
                    @Authorization(value = "ServiceAuthorization")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "A representation of the created organisation",
                    response = OrganisationResponse.class
            )
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseEntity<OrganisationResponse> createOrganisation(
            @Valid @NotNull @RequestBody OrganisationCreationRequest organisationCreationRequest) {

        log.info("Received request to create a new organisation...");

        organisationCreationRequestValidator.validate(organisationCreationRequest);

        OrganisationResponse organisationResponse =
                organisationService.createOrganisationFrom(organisationCreationRequest);

        log.info("Received response to create a new organisation..." + organisationResponse);
        return ResponseEntity
                .status(201)
                .body(organisationResponse);
    }

    @ApiOperation(
            value = "Retrieves organisation details",
            authorizations = {
                    @Authorization(value = "ServiceAuthorization")
            }
    )
    @ApiParam(
            allowEmptyValue = true,
            required = true
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Details of one or more organisations",
                    response = OrganisationsDetailResponse.class
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> retrieveOrganisations(@RequestParam(required = false) String id) {
        Object organisationResponse;
        if (id == null) {
            log.info("Received request to retrieve all organisations");
            organisationResponse =
                    organisationService.retrieveOrganisations();
        } else {
            log.info("Received request to retrieve organisation with ID " + id);

            organisationCreationRequestValidator.validateOrganisationIdentifier(id);
            organisationResponse =
                    organisationService.retrieveOrganisation(id);
        }

        log.debug("Received response to retrieve organisation details" + organisationResponse);
        return ResponseEntity
                .status(200)
                .body(organisationResponse);
    }

    @ApiOperation(
            value = "Retrieves the user with the given email address if organisation is active",
            authorizations = {
                    @Authorization(value = "ServiceAuthorization")
            }
    )
    @ApiParam(
            name = "email",
            type = "string",
            value = "The email address of the user to return",
            required = true
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "A representation of a professional user",
                    response = ProfessionalUsersResponse.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "An invalid email address was provided"
            ),
            @ApiResponse(
                    code = 404,
                    message = "No user was found with the provided email address"
            )
    })
    @GetMapping(
            value = "/users",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<ProfessionalUsersResponse> findUserByEmail(@RequestParam(value = "email") String email) {

        ProfessionalUser user = professionalUserService.findProfessionalUserByEmailAddress(email);

        if (user == null || user.getOrganisation().getStatus() != OrganisationStatus.ACTIVE) {
            throw new EmptyResultDataAccessException(1);
        }
        return ResponseEntity
                .status(200)
                .body(new ProfessionalUsersResponse(user));
    }

    @ApiOperation(
            value = "Retrieves an organisations payment accounts by super user email",
            authorizations = {
                    @Authorization(value = "ServiceAuthorization")
            }
    )
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "The organisations associated payment accounts",
                    response = OrganisationPbaResponse.class
            )
    })
    @GetMapping(
            path = "/pbas",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<OrganisationPbaResponse> retrievePaymentAccountBySuperUserEmail(@NotNull @RequestParam("email") String email) {
        log.info("Received request to retrieve an organisations payment accounts by email...");

        Organisation organisation = paymentAccountService.findPaymentAccountsByEmail(email);
        if (null == organisation || organisation.getPaymentAccounts().isEmpty()) {

            throw new EmptyResultDataAccessException(1);
        }
        return ResponseEntity
                .status(200)
                .body(new OrganisationPbaResponse(organisation, false));
    }

    @ApiOperation(
        value = "Updates an organisation",
        authorizations = {
            @Authorization(value = "ServiceAuthorization")
        })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Updated an organisation"),
            @ApiResponse(code = 404, message = "If Organisation is not found"),
            @ApiResponse(code = 400, message = "If Organisation request sent with null/invalid values for mandatory fields")
    })
    @PutMapping(
            value = "/{orgId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseEntity<?> updatesOrganisation(
            @Valid @NotNull @RequestBody OrganisationCreationRequest organisationCreationRequest,
            @PathVariable("orgId") @NotBlank String organisationIdentifier) {

        log.info("Received request to update organisation for organisationIdentifier: " + organisationIdentifier);
        organisationCreationRequestValidator.validate(organisationCreationRequest);
        organisationCreationRequestValidator.validateOrganisationIdentifier(organisationIdentifier);
        Organisation existingOrganisation = organisationService.getOrganisationByOrganisationIdentifier(organisationIdentifier);
        updateOrganisationRequestValidator.validateStatus(existingOrganisation, organisationCreationRequest.getStatus(), organisationIdentifier);

        ProfessionalUser professionalUser = existingOrganisation.getUsers().get(0);
        if (existingOrganisation.getStatus().isPending() && organisationCreationRequest.getStatus().isActive()) {
            log.info("Organisation is getting activated");
            UserProfileCreateResponse userProfileCreateResponse = createUserProfileFor(existingOrganisation, professionalUser);
            if (userProfileCreateResponse != null && userProfileCreateResponse.getIdamRegistrationResponse() == HttpStatus.CREATED.value()) {
                professionalUser.setUserIdentifier(userProfileCreateResponse.getIdamId());
                professionalUserService.persistUser(professionalUser);
                organisationService.updateOrganisation(organisationCreationRequest, organisationIdentifier);
                return ResponseEntity.status(200).build();
            } else {
                if (userProfileCreateResponse == null) {
                    log.error("User Profile failed!!");
                } else {
                    log.error("Idam returned status code : " + userProfileCreateResponse.getIdamRegistrationResponse());
                }
                return ResponseEntity.status(500).build();
            }
        } else {
            organisationService.updateOrganisation(organisationCreationRequest, organisationIdentifier);
            return ResponseEntity.status(200).build();
        }
    }

    private UserProfileCreateResponse createUserProfileFor(Organisation existingOrganisation, ProfessionalUser professionalUser) {
        log.info("Creating user...");
        UserProfileCreateResponse userProfileCreateResponse = null;
        UserProfileCreationRequest userCreationRequest = anUserProfileCreationRequest()
                .email(professionalUser.getEmailAddress())
                .firstName(professionalUser.getFirstName())
                .lastName(professionalUser.getLastName())
                .languagePreference(LanguagePreference.EN)
                .userCategory(UserCategory.PROFESSIONAL)
                .userType(UserType.EXTERNAL)
                .idamRoles(prdEnumService.getPrdEnumByEnumType("PRD_ROLE"))
                .build();

        ResponseEntity responseEntity = userProfileFeignClient.createUserProfile(userCreationRequest);

        if (HttpStatus.CREATED == responseEntity.getStatusCode()) {
            userProfileCreateResponse = new ObjectMapper().convertValue(responseEntity.getBody(), UserProfileCreateResponse.class);
            log.info("UserProfile Response success. IDAM_ID:" + userProfileCreateResponse.getIdamId() + " IDAM_REGISTRATION_CODE:" + userProfileCreateResponse.getIdamRegistrationResponse());
        } else {
            log.info("UserProfile returned status code = " + responseEntity.getStatusCode());
        }
        return userProfileCreateResponse;
    }

    @ApiOperation(
            value = "Retrieves the organisation details with the given status ",
            authorizations = {
                    @Authorization(value = "ServiceAuthorization")
            }
    )
    @ApiParam(
            name = "status",
            type = "string",
            value = "The organisation details of the status to return",
            required = true

    )

    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "A representation of a organisation ",
                    response = OrganisationsDetailResponse.class
            ),
            @ApiResponse(
                    code = 200,
                    message = "No organisation details found with the provided status "
            ),
            @ApiResponse(
                    code = 400,
                    message = "Invalid status provided for an organisation"
            )
    })
    @GetMapping(
            params = {"status"},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public ResponseEntity<OrganisationsDetailResponse> getAllOrganisationDetailsByStatus(@NotNull @RequestParam("status") String status) {

        OrganisationsDetailResponse organisationsDetailResponse;
        if (organisationCreationRequestValidator.contains(status.toUpperCase())) {

            organisationsDetailResponse =
                    organisationService.findByOrganisationStatus(OrganisationStatus.valueOf(status.toUpperCase()));
        } else {
            log.error("Invalid Request param for status field");
            throw new InvalidRequest("400");
        }
        log.info("Received response for status...");
        return ResponseEntity.status(200).body(organisationsDetailResponse);
    }

    @ApiOperation(
        value = "Add a user to an organisation",
        authorizations = {
            @Authorization(value = "ServiceAuthorization")
        }
    )
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "User has been added",
                    response = OrganisationResponse.class
            )
    })
    @PostMapping(
            path = "/{orgId}/users/",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public ResponseEntity<NewUserResponse> addUserToOrganisation(
            @Valid @NotNull @RequestBody NewUserCreationRequest newUserCreationRequest,
            @PathVariable("orgId") @NotBlank String organisationIdentifier) {

        log.info("Received request to add a new user to an organisation..." + organisationIdentifier);

        organisationCreationRequestValidator.validateOrganisationIdentifier(organisationIdentifier);

        Organisation existingOrganisation = organisationService.getOrganisationByOrganisationIdentifier(organisationIdentifier);
        updateOrganisationRequestValidator.validateStatus(existingOrganisation, null, organisationIdentifier);

        List<PrdEnum> prdEnumList = prdEnumService.findAllPrdEnums();

        if (UserCreationRequestValidator.contains(newUserCreationRequest.getRoles(), prdEnumList).isEmpty()) {
            log.error("Invalid/No user role(s) provided");
            throw new InvalidRequest("404");
        } else {
            NewUserResponse newUserResponse =
                    professionalUserService.addNewUserToAnOrganisation(newUserCreationRequest, organisationIdentifier);

            log.info("Received request to add a new user to an organisation..." + newUserResponse);
            return ResponseEntity
                    .status(201)
                    .body(newUserResponse);
        }
    }
}
