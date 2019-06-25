package uk.gov.hmcts.reform.sidam.controller;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.sidam.response.UserResponse;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@RestController
public class SidamController {

    @ApiOperation("Register a User Profile")
    @ApiResponses({
            @ApiResponse(
                    code = 201,
                    message = "Register a User Profile using request body"
            ),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request",
                    response = String.class
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error",
                    response = String.class
            )
    })

    @PostMapping(
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE,
            path = "/user/registration"
    )
    @ResponseBody
    public ResponseEntity<Object> createUserProfile(@Valid @RequestBody Object createUserProfileData) {
        log.info("Creating new User Profile");

        requireNonNull(createUserProfileData, "createUserProfileData cannot be null");

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @ApiOperation("Get a User by email")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Register a User Profile using request body",
                    response =  UserResponse.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request",
                    response = String.class
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error",
                    response = String.class
            )
    })

    @GetMapping(
            produces = APPLICATION_JSON_UTF8_VALUE,
            path = "/api/v1/users",
            params = "email"
    )
    public ResponseEntity<Object> getUserByEmail(@RequestParam String email){
        log.info("Get User by id");
        UserResponse responseBody = UserResponse.builder().active(true).email(email)
                .forename("Prashanth").id(UUID.randomUUID().toString()).surname("Kotla").locked(false).roles(new ArrayList<>()).build();
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

    @ApiOperation("Get a User by Id")
    @ApiResponses({
            @ApiResponse(
                    code = 200,
                    message = "Register a User Profile using request body",
                    response =  UserResponse.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request",
                    response = String.class
            ),
            @ApiResponse(
                    code = 500,
                    message = "Internal Server Error",
                    response = String.class
            )
    })

    @GetMapping(
            produces = APPLICATION_JSON_UTF8_VALUE,
            path = "/api/v1/users/{userId}"
    )
    public ResponseEntity<Object> getUserById(@PathVariable String userId){
        log.info("Get User by id");
        List<String> roles = new ArrayList<String>();
        roles.add("pui-organisation-manager");
        UserResponse responseBody = UserResponse.builder().active(true).email("shreedhar.lomte@hmcts.net")
                .forename("Shreedhar").id(userId).surname("Lomte").locked(false).roles(roles).build();
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }

}
