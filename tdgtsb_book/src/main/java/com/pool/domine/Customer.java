package com.pool.domine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer implements Serializable {
    private Integer customerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String ssn;
    private String emailAddress;
    private String homePhone;
    private String cellPhone;
    private String workPhone;
    private String notificationPref;

}
