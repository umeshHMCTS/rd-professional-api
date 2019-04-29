package uk.gov.hmcts.reform.professionalapi.domain.entities;

import static javax.persistence.CascadeType.*;
import static javax.persistence.GenerationType.AUTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "payment_account")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PaymentAccount {

    @Id
    @GeneratedValue(strategy = AUTO)
    private UUID id;

    @Column(name = "PBA_NUMBER")
    private String pbaNumber;

    @ManyToOne
    @JoinColumn(name = "ORGANISATION_ID")
    private Organisation organisation;

    @ManyToMany(
            mappedBy = "paymentAccounts",
            cascade = {PERSIST, MERGE})
    private List<ProfessionalUser> users = new ArrayList<>();

    @LastModifiedDate
    @Column(name = "LAST_UPDATED")
    private LocalDateTime lastUpdated;

    @CreatedDate
    @Column(name = "CREATED")
    private LocalDateTime created;

    public PaymentAccount(String pbaNumber) {
        this.pbaNumber = pbaNumber;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void addUser(ProfessionalUser user) {
        this.users.add(user);
    }

    public UUID getId() {
        return id;
    }

    public String getPbaNumber() {
        return pbaNumber;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public List<ProfessionalUser> getUsers() {
        return users;
    }
}
