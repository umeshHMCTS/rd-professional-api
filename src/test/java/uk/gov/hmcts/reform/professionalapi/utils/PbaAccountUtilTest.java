package uk.gov.hmcts.reform.professionalapi.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import uk.gov.hmcts.reform.professionalapi.domain.PaymentAccount;
import uk.gov.hmcts.reform.professionalapi.domain.UserAccountMap;
import uk.gov.hmcts.reform.professionalapi.domain.UserAccountMapId;
import uk.gov.hmcts.reform.professionalapi.util.PbaAccountUtil;

public class PbaAccountUtilTest {

    @Test
    public void shouldReturnPaymentAccountsFromUserAccountMap() {

        PaymentAccount paymentAccount = mock(PaymentAccount.class);
        UserAccountMapId userAccountMapId = mock(UserAccountMapId.class);

        UserAccountMap userAccountMap = mock(UserAccountMap.class);

        List<UserAccountMap> userAccountMaps = new ArrayList<>();
        userAccountMaps.add(userAccountMap);

        when(userAccountMap.getUserAccountMapId()).thenReturn(userAccountMapId);
        when(userAccountMapId.getPaymentAccount()).thenReturn(paymentAccount);
        List<PaymentAccount> paymentAccounts = PbaAccountUtil.getPaymentAccountsFromUserAccountMap(userAccountMaps);
        assertThat(paymentAccounts.size()).isGreaterThan(0);
    }

    @Test
    public void shouldReturnPaymentAccountFromUserMap() {

        final UUID paymentAccountUuid = UUID.randomUUID();

        PaymentAccount paymentAccountMock = mock(PaymentAccount.class);

        when(paymentAccountMock.getId()).thenReturn(paymentAccountUuid);

        List<PaymentAccount> userMapPaymentAccount = new ArrayList<>();

        userMapPaymentAccount.add(paymentAccountMock);

        List<PaymentAccount> paymentAccountsEntity = new ArrayList<>();

        paymentAccountsEntity.add(paymentAccountMock);

        List<PaymentAccount> paymentAccounts = PbaAccountUtil.getPaymentAccountFromUserMap(userMapPaymentAccount, paymentAccountsEntity);

        assertThat(paymentAccounts.size()).isGreaterThan(0);
    }

    @Test
    public void shouldReturnPaymentAccountFromOrganisationUser() {

        PaymentAccount paymentAccountMock = mock(PaymentAccount.class);

        List<PaymentAccount> paymentAccountsEntity = new ArrayList<>();

        paymentAccountsEntity.add(paymentAccountMock);

        if (!paymentAccountsEntity.isEmpty()) {

            List<PaymentAccount> paymentAccounts = PbaAccountUtil.getPaymentAccount(paymentAccountsEntity);
            assertThat(paymentAccounts.size()).isGreaterThan(0);
        }
    }

    @Test
    public void removeEmptyWhiteSpacesTest() {

        assertThat(PbaAccountUtil.removeEmptySpaces(" Test ")).isEqualTo("Test");
        assertThat(PbaAccountUtil.removeEmptySpaces(null)).isEqualTo(null);
        assertThat(PbaAccountUtil.removeEmptySpaces(" Te  st ")).isEqualTo("Te st");

    }

    @Test
    public void removeAllWhiteSpacesTest() {

        assertThat(PbaAccountUtil.removeAllSpaces(" T e s t    1 ")).isEqualTo("Test1");
        assertThat(PbaAccountUtil.removeAllSpaces(null)).isEqualTo(null);

    }

    @Test(expected = AccessDeniedException.class)
    public void shouldReturnTrueValidateOrgIdentifier() {
        String uuid = UUID.randomUUID().toString();
        PbaAccountUtil.validateOrgIdentifier(uuid,UUID.randomUUID().toString());
    }
}
