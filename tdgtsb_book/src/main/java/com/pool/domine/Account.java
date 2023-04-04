package com.pool.domine;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements Serializable {
    private Integer accountId;
    private Float balance;
    private Timestamp lastStatementDate;
}
