package uk.gov.hmcts.reform.professionalapi.controller.response;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.professionalapi.helpers.ReflectionHelper.getPrivateField;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.professionalapi.controller.advice.ErrorResponse;

public class ErrorResponseTest {

    @Test
    public void testErrorResponse() throws Exception {
        ErrorResponse errorDetails = new ErrorResponse(42, HttpStatus.OK, "msg", "desc","time");

        assertThat((String) getPrivateField(errorDetails, "errorDescription")).isEqualTo("desc");
        assertThat((String) getPrivateField(errorDetails, "errorMessage")).isEqualTo("msg");
        assertThat((String) getPrivateField(errorDetails, "timeStamp")).isEqualTo("time");
    }


}
