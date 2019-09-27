package uk.gov.hmcts.reform.professionalapi.client;

import java.util.ArrayList;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import uk.gov.hmcts.reform.professionalapi.idam.IdamClient;

@Slf4j
public class ProfessionalApiClient {
    private final String s2sToken;

    protected IdamClient idamClient;

    public ProfessionalApiClient(String s2sToken, IdamClient idamClient) {
        this.s2sToken = s2sToken;
        this.idamClient = idamClient;
    }

    public List<String> getS2sAndBearer() {
        List<String> tokens = new ArrayList<>();
        tokens.add("Bearer " + idamClient.getInternalBearerToken());
        tokens.add(s2sToken);
        return tokens;
    }
}