package uk.gov.hmcts.reform.professionalapi;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.professionalapi.client.ProfessionalApiClient;

import java.util.List;

@RunWith(SpringIntegrationSerenityRunner.class)
@ActiveProfiles("functional")
@Slf4j
public class GenerateAuthTokens extends AuthorizationFunctionalTest {
    
    @Test
    public void generateAuthTokens() {
        List<String> tokens = professionalApiClient.getS2sAndBearer();
        String bearerToken = tokens.get(0);
        String s2sToken = tokens.get(1);
        log.info("BEARER TOKEN: " + bearerToken);
        log.info("S2S TOKEN: " + s2sToken);
    }
}
