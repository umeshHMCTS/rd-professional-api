package uk.gov.hmcts.reform.professionalapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.hmcts.reform.professionalapi.controller.request.UserProfileCreationRequest;
import uk.gov.hmcts.reform.professionalapi.controller.response.UserProfileCreateResponse;

@FeignClient(name = "UserProfileClient", url = "rd-user-profile-api:8091")
public interface UserProfileFeignClient {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, path = "/v1/userprofile")
    ResponseEntity<UserProfileCreateResponse> createUserProfile(@RequestBody UserProfileCreationRequest userProfileCreationRequest);
}
