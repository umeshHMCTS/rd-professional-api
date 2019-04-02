package uk.gov.hmcts.reform.professionalapi.domain.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PaymentAccountTest {

    @Test
    public void creates_payment_account_correctly() {

        PaymentAccount paymentAccount = new PaymentAccount("some-pba-number");

        assertThat(paymentAccount.getPbaNumber()).isEqualTo("some-pba-number");              // hibernate generated

        assertThat(paymentAccount.getId()).isNull(); // hibernate generated
        assertThat(paymentAccount.getCreated()).isNull(); // hibernate generated
        assertThat(paymentAccount.getLastUpdated()).isNull(); // hibernate generated
    }

    @Test
    public void can_set_user() {
        PaymentAccount paymentAccount = new PaymentAccount();

        ProfessionalUser user = new ProfessionalUser();

        paymentAccount.setUser(user);

        assertThat(paymentAccount.getUser()).isSameAs(user);
    }

    @Test
    public void can_set_organisation() {
        PaymentAccount paymentAccount = new PaymentAccount();

        Organisation organisation = new Organisation();

        paymentAccount.setOrganisation(organisation);

        assertThat(paymentAccount.getOrganisation()).isSameAs(organisation);
    }
}