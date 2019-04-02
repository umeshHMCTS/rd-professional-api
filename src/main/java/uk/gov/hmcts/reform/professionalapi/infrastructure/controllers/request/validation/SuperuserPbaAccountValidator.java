package uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.validation;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.OrganisationCreationRequest;
import uk.gov.hmcts.reform.professionalapi.infrastructure.controllers.request.PbaAccountCreationRequest;

@Service
public class SuperuserPbaAccountValidator implements OrganisationRequestValidator {

    @Override
    public void validate(OrganisationCreationRequest organisationCreationRequest) {

        List<PbaAccountCreationRequest> pbaAccounts = organisationCreationRequest.getPbaAccounts();

        PbaAccountCreationRequest userPbaAccount = organisationCreationRequest.getSuperUser().getPbaAccount();

        if (userPbaAccount != null) {

            if (pbaAccounts == null) {
                throw new InvalidRequest("Super user pba account number not in the organisations accounts");
            }

            pbaAccounts.stream()
                    .filter(organisationAccount -> organisationAccount
                            .getPbaNumber()
                            .equals(userPbaAccount.getPbaNumber()))

                    .findFirst().orElseThrow(() -> new InvalidRequest("Super user pba account number not in the organisations accounts"));
        }
    }
}
