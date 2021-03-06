package uk.gov.hmcts.reform.professionalapi.controller.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class MultipleUserProfilesResponseTest {

    private UserProfile userProfileMock = mock(UserProfile.class);

    @Test
    public void multipleUserProfileResponseTest() {
        List<UserProfile> userProfiles = new ArrayList<>();
        userProfiles.add(userProfileMock);

        MultipleUserProfilesResponse multipleUserProfilesResponse = new MultipleUserProfilesResponse(userProfiles, true);

        assertThat(multipleUserProfilesResponse.getUserProfiles().size()).isEqualTo(1);
    }

    @Test
    public void test_MultipleUserProfilesResponseSetter() {
        List<UserProfile> userProfiles = new ArrayList<>();
        userProfiles.add(userProfileMock);

        List<GetUserProfileResponse> getUserProfileResponse = new ArrayList<>();
        getUserProfileResponse.add(mock(GetUserProfileResponse.class));

        MultipleUserProfilesResponse multipleUserProfilesResponse = new MultipleUserProfilesResponse(userProfiles, true);

        multipleUserProfilesResponse.setUserProfiles(getUserProfileResponse);

        assertThat(multipleUserProfilesResponse.getUserProfiles()).isEqualTo(getUserProfileResponse);
    }

}
